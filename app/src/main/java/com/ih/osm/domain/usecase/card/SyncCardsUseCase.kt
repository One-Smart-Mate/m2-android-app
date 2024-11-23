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
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

interface SyncCardsUseCase {
    suspend operator fun invoke(handleNotification: Boolean = true)
}

class SyncCardsUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val notificationManager: NotificationManager,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val saveCardSolutionUseCase: SaveCardSolutionUseCase,
    private val evidenceRepo: EvidenceRepository,
    private val solutionRepo: SolutionRepository
) : SyncCardsUseCase {
    override suspend fun invoke(handleNotification: Boolean) {

        var selectedCard: Card? = null

        try {
            val cardList = cardRepository.getAllLocal()
            Log.e("test", "CardList -> ${cardList.isEmpty()}")
            cardList.forEach { card ->
                selectedCard = card
                val evidences = mutableListOf<CreateEvidenceRequest>()
                Log.e("test", "Current card.. $card")
                card.evidences?.forEach { evidence ->
                    val url = firebaseStorageRepository.uploadEvidence(evidence)
                    Log.e("test", "saving evidence.. $url")
                    if (url.isNotEmpty()) {
                        evidences.add(CreateEvidenceRequest(evidence.type, url))
                        evidenceRepo.delete(evidence.id)
                    }
                }

                val remoteCard = if (card.isLocalCard()) {
                    val cardRequest = card.toCardRequest(evidences)
                    fileHelper.logCreateCardRequest(cardRequest)
                    Log.e("test", "Current card request.. $cardRequest")
                    firebaseAnalyticsHelper.logCreateRemoteCardRequest(cardRequest)
                    val networkCard = cardRepository.saveRemote(cardRequest)
                    cardRepository.delete(card.uuid)
                    cardRepository.save(networkCard)
                    fileHelper.logCreateCardRequestSuccess(networkCard)
                    firebaseAnalyticsHelper.logCreateRemoteCard(networkCard)
                    networkCard
                } else {
                    card
                }
                Log.e("test", "Current card remote.. $remoteCard")

                val solutions = solutionRepo.getAllByCard(card.uuid)
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
                solutionRepo.deleteAllByCard(card.uuid)
                Log.e("test", "saving card.. $remoteCard")
                notificationManager.buildNotificationSuccessCard(remoteCard.siteCardId.toString())
            }
        } catch (e: Exception) {
            Log.e("test", "saving card exception.. ${e.localizedMessage}")
            restoreEvidences(selectedCard)
            firebaseAnalyticsHelper.logSyncCardException(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            fileHelper.logException(e)
            notificationManager.buildErrorNotification(e.localizedMessage.orEmpty())
        }
    }

    private suspend fun restoreEvidences(selectedCard: Card?) {
        firebaseStorageRepository.deleteEvidence(selectedCard?.uuid.orEmpty())
        selectedCard?.evidences?.forEach { evidence ->
            evidenceRepo.save(evidence)
        }
    }
}
