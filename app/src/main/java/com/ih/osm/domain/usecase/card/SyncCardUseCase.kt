package com.ih.osm.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.toCardRequest
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import javax.inject.Inject

interface SyncCardUseCase {
    suspend operator fun invoke(card: Card, handleNotification: Boolean = false): Card?
}

class SyncCardUseCaseImpl
@Inject
constructor(
    private val cardRepo: CardRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val notificationManager: NotificationManager,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val evidenceRepo: EvidenceRepository
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
                    evidenceRepo.delete(evidence.id)
                }
            }
            val cardRequest = card.toCardRequest(evidences)
            fileHelper.logCreateCardRequest(cardRequest)
            Log.e("test", "Current card request.. $cardRequest")
            val remoteCard = cardRepo.saveRemote(cardRequest)
            firebaseAnalyticsHelper.logCreateRemoteCardRequest(cardRequest)
            Log.e("test", "Current card remote.. $remoteCard")
            cardRepo.save(remoteCard)
            firebaseAnalyticsHelper.logCreateRemoteCard(remoteCard)
            Log.e("test", "saving card.. $remoteCard")
            if (handleNotification) {
                notificationManager.buildNotificationSuccessCard()
            }
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
