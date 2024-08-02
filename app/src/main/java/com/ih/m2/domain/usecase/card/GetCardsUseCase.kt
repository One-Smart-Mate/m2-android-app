package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.usecase.notifications.GetFirebaseNotificationUseCase
import javax.inject.Inject

interface GetCardsUseCase {
    suspend operator fun invoke(
        syncRemote: Boolean = false,
        localCards: Boolean = false
    ): List<Card>
}

class GetCardsUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val notificationUseCase: GetFirebaseNotificationUseCase
) : GetCardsUseCase {

    override suspend fun invoke(syncRemote: Boolean, localCards: Boolean): List<Card> {
        if (syncRemote && NetworkConnection.isConnected()) {
            Log.e("test","Getting and Sync Remote Cards..")
            val siteId = localRepository.getSiteId()
            val remoteCards = cardRepository.getCardsByUser(siteId)
            localRepository.saveCards(remoteCards)
            notificationUseCase(remove = true, syncCards = true)
        }
        return if (localCards) {
            localRepository.getLocalCards()
        } else {
            localRepository.getCards()
        }
    }
}