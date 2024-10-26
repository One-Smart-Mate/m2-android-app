package com.ih.osm.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.isLocalCard
import com.ih.osm.domain.model.isRemoteCard
import com.ih.osm.domain.model.toCardRequest
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SyncCardsUseCase {
    suspend operator fun invoke(cardList: List<Card>, handleNotification: Boolean = true)
}

class SyncCardsUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val notificationManager: NotificationManager,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val saveCardSolutionUseCase: SaveCardSolutionUseCase
) : SyncCardsUseCase {
    override suspend fun invoke(cardList: List<Card>, handleNotification: Boolean) {
        var currentProgress = 0f
        val id =
            if (handleNotification && cardList.isNotEmpty()) {
                notificationManager.buildProgressNotification()
            } else {
                0
            }
        val progressByCard: Float = 100f / cardList.size
        var selectedCard: Card? = null
        try {
            Log.e("test", "CardList -> ${cardList.isEmpty()}")
            cardList.forEach { card ->
                selectedCard = card
                if (handleNotification) {
                    currentProgress += progressByCard
                    notificationManager.updateNotificationProgress(
                        notificationId = id,
                        currentProgress = currentProgress.toInt()
                    )
                }

                val evidences = mutableListOf<CreateEvidenceRequest>()
                Log.e("test", "Current card.. $card")
                card.evidences?.forEach { evidence ->
                    val url = firebaseStorageRepository.uploadEvidence(evidence)
                    Log.e("test", "saving evidence.. $url")
                    if (url.isNotEmpty()) {
                        evidences.add(CreateEvidenceRequest(evidence.type, url))
                        localRepository.deleteEvidence(evidence.id)
                    }
                }

                val remoteCard = if (card.isLocalCard()) {
                    val cardRequest = card.toCardRequest(evidences)
                    fileHelper.logCreateCardRequest(cardRequest)
                    Log.e("test", "Current card request.. $cardRequest")
                    firebaseAnalyticsHelper.logCreateRemoteCardRequest(cardRequest)
                    val networkCard = cardRepository.saveCard(cardRequest)
                    localRepository.deleteCard(card.uuid)
                    localRepository.saveCard(networkCard)
                    fileHelper.logCreateCardRequestSuccess(networkCard)
                    firebaseAnalyticsHelper.logCreateRemoteCard(networkCard)
                    networkCard
                } else {
                    card
                }
                Log.e("test", "Current card remote.. $remoteCard")

                val solutions = localRepository.getCardSolutions(card.uuid)
                solutions.forEach {
                    saveCardSolutionUseCase(
                        solutionType = it.solutionType,
                        cardId = remoteCard.id,
                        userSolutionId = it.userSolutionId,
                        comments = it.comments,
                        remoteEvidences = if (card.isRemoteCard()) {
                            evidences.toList()
                        } else {
                            emptyList()
                        },
                        saveLocal = false
                    )
                }
                localRepository.deleteSolutions(card.uuid)
                Log.e("test", "saving card.. $remoteCard")
            }
        } catch (e: Exception) {
            Log.e("test", "saving card exception.. ${e.localizedMessage}")
            restoreEvidences(selectedCard)
            firebaseAnalyticsHelper.logSyncCardException(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            fileHelper.logException(e)
            if (cardList.isNotEmpty()) {
                notificationManager.buildErrorNotification(id, e.localizedMessage.orEmpty())
            }
        }
    }

    private suspend fun restoreEvidences(selectedCard: Card?) {
        firebaseStorageRepository.deleteEvidence(selectedCard?.uuid.orEmpty())
        selectedCard?.evidences?.forEach { evidence ->
            localRepository.saveEvidence(evidence)
        }
    }
}
