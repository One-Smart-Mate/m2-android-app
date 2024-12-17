package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import javax.inject.Inject

interface GetCardsUseCase {
    suspend operator fun invoke(
        syncRemote: Boolean = false,
        localCards: Boolean = false,
    ): List<Card>
}

class GetCardsUseCaseImpl
    @Inject
    constructor(
        private val repo: CardRepository,
        private val notificationUseCase: GetFirebaseNotificationUseCase,
    ) : GetCardsUseCase {
        override suspend fun invoke(
            syncRemote: Boolean,
            localCards: Boolean,
        ): List<Card> {
            if (syncRemote && NetworkConnection.isConnected()) {
                val remoteCards = repo.getAllRemoteByUser()
                repo.saveAll(remoteCards)
                notificationUseCase(remove = true, syncCards = true)
            }
            return if (localCards) {
                repo.getAllLocal()
            } else {
                repo.getAll()
            }
        }
    }
