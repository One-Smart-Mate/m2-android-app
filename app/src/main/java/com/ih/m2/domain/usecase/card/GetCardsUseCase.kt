package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardsUseCase {
    suspend operator fun invoke(
        syncRemote: Boolean = false
    ): List<Card>
}

class GetCardsUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardsUseCase {

    override suspend fun invoke(syncRemote: Boolean): List<Card> {
        if (syncRemote) {
            Log.e("test","Getting and Sync Remote Cards..")
            val siteId = localRepository.getSiteId()
            val remoteCards = cardRepository.getCardsByUser(siteId)
            localRepository.saveCards(remoteCards)
        }
        return localRepository.getCards()
    }
}