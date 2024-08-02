package com.ih.m2.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.FileHelper
import com.ih.m2.core.notifications.NotificationManager
import com.ih.m2.core.ui.functions.CustomException
import com.ih.m2.data.model.CreateEvidenceRequest
import com.ih.m2.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.toCardRequest
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SyncCardsUseCase {
    suspend operator fun invoke(cardList: List<Card>, handleNotification: Boolean = true)
}

class SyncCardsUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val notificationManager: NotificationManager,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper
) : SyncCardsUseCase {

    override suspend fun invoke(cardList: List<Card>, handleNotification: Boolean) {
        var currentProgress = 0f
        val id = if (handleNotification && cardList.isNotEmpty()) {
            notificationManager.buildProgressNotification()
        } else {
            0
        }
        val progressByCard: Float = 100f / cardList.size

        try {
            cardList.forEach { card ->
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
                val cardRequest = card.toCardRequest(evidences)
                fileHelper.logCreateCardRequest(cardRequest)
                Log.e("test", "Current card request.. ${cardRequest}")
                val remoteCard = cardRepository.saveCard(cardRequest)
                firebaseAnalyticsHelper.logCreateRemoteCardRequest(cardRequest)
                Log.e("test", "Current card remote.. $remoteCard")
                localRepository.deleteCard(card.uuid)
                localRepository.saveCard(remoteCard)
                if (handleNotification) {
                    currentProgress += progressByCard
                    notificationManager.updateNotificationProgress(
                        notificationId = id,
                        currentProgress = currentProgress.toInt(),
                    )
                }
                fileHelper.logCreateCardRequestSuccess(remoteCard)
                firebaseAnalyticsHelper.logCreateRemoteCard(remoteCard)
                Log.e("test", "saving card.. $remoteCard")
            }
        } catch (e: Exception) {
            Log.e("test", "saving card exception.. ${e.localizedMessage}")
            firebaseAnalyticsHelper.logSyncCardException(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            fileHelper.logException(e)
            if (cardList.isNotEmpty()) {
                notificationManager.buildErrorNotification(id, e.localizedMessage.orEmpty())
            }
        }
    }
}