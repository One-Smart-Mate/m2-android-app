package com.osm.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.osm.core.file.FileHelper
import com.osm.core.notifications.NotificationManager
import com.osm.data.model.CreateEvidenceRequest
import com.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.osm.domain.model.Card
import com.osm.domain.model.toCardRequest
import com.osm.domain.repository.cards.CardRepository
import com.osm.domain.repository.firebase.FirebaseStorageRepository
import com.osm.domain.repository.local.LocalRepository
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
            fileHelper.logCreateCardRequestSuccess(remoteCard)
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