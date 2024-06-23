package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SyncCardsUseCase {
    suspend operator fun invoke(cardList: List<Card>)
}

class SyncCardsUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) : SyncCardsUseCase {

    override suspend fun invoke(cardList: List<Card>) {
        cardList.forEach { card ->
            val evidences = mutableListOf<Pair<String,String>>()
            card.evidences?.forEach { evidence ->
                val url = firebaseStorageRepository.uploadEvidence(evidence)
                Log.e("test","saving evidence.. $evidence")
                if (url.isNotEmpty()) {
                    evidences.add(Pair(url,evidence.type))
                    localRepository.deleteEvidence(evidence.id)
                }
            }
            Log.e("test","saving card.. $card")
            localRepository.deleteCard(card.id)
            //  Log.e("test","saving evidencess.. $evidences")
        }
    }
}