package com.ih.m2.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.FileHelper
import com.ih.m2.core.notifications.NotificationManager
import com.ih.m2.data.model.CreateEvidenceRequest
import com.ih.m2.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.toCardRequest
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SyncCardUseCase {
    suspend operator fun invoke(card: Card, handleNotification: Boolean = true): Card?
}

class SyncCardUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val notificationManager: NotificationManager,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper
) : SyncCardUseCase {

    override suspend fun invoke(card: Card, handleNotification: Boolean): Card? {
        return try {
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
            localRepository.saveCard(remoteCard)
            firebaseAnalyticsHelper.logCreateRemoteCard(remoteCard)
            Log.e("test", "saving card.. $remoteCard")
            notificationManager.buildNotificationSuccessCard()
            remoteCard
        } catch (e: Exception) {
            Log.e("test", "saving card exception.. ${e.localizedMessage}")
            firebaseAnalyticsHelper.logSyncCardException(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            fileHelper.logException(e)
            null
        }
    }
}