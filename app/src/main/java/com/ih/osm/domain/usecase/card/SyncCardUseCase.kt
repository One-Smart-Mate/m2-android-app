package com.ih.osm.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.isLocalCard
import com.ih.osm.domain.model.toCardRequest
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import javax.inject.Inject

interface SyncCardUseCase {
    suspend operator fun invoke(card: Card): Card?
}

class SyncCardUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        private val evidenceRepository: EvidenceRepository,
    ) : SyncCardUseCase {
        override suspend fun invoke(card: Card): Card? {
            return try {
                val evidences = mutableListOf<CreateEvidenceRequest>()
                card.evidences?.forEach { evidence ->
                    val url = firebaseStorageRepository.uploadEvidence(evidence)
                    if (url.isNotEmpty()) {
                        evidences.add(CreateEvidenceRequest(evidence.type, url))
                        evidenceRepository.delete(evidence.id)
                    }
                }
                Log.d("SyncCardUseCase", "Es local: ${card.isLocalCard()}")

                if (card.isLocalCard()) {
                    val cardRequest =
                        try {
                            Log.d("SyncCardUseCase", "Transformando Card a CardRequest...")
                            card.toCardRequest(evidences)
                        } catch (e: Exception) {
                            Log.e("SynCardUseCase", "Error en toCardRequest: ${e.message}")
                            FirebaseCrashlytics.getInstance().recordException(e)
                            return null
                        }
                    Log.d("SyncCardUseCase", "CardRequest: $cardRequest")
                    LoggerHelperManager.logCreateCardRequest(cardRequest)
                    firebaseAnalyticsHelper.logCreateRemoteCardRequest(cardRequest)
                    Log.d("SyncCardUseCase", "Subiendo tarjeta remota...")
                    val remoteCard = cardRepository.saveRemote(cardRequest)
                    Log.d("SyncCardUseCase", "Tarjeta subida: $remoteCard")
                    cardRepository.delete(card.uuid)
                    cardRepository.save(remoteCard)

                    LoggerHelperManager.logCreateCardRequestSuccess(remoteCard)
                    firebaseAnalyticsHelper.logCreateRemoteCard(remoteCard)

                    remoteCard
                } else {
                    card
                }
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
