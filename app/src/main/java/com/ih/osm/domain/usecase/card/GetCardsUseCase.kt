package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import javax.inject.Inject

interface GetCardsUseCase {
    suspend operator fun invoke(
        syncRemote: Boolean = false,
        localCards: Boolean = false
    ): List<Card>
}

class GetCardsUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val localRepo: LocalCardRepository,
    private val authRepo: AuthRepository,
    private val notificationUseCase: GetFirebaseNotificationUseCase
) : GetCardsUseCase {
    override suspend fun invoke(syncRemote: Boolean, localCards: Boolean): List<Card> {
        if (syncRemote && NetworkConnection.isConnected()) {
            val siteId = authRepo.getSiteId()
            val remoteCards = remoteRepo.getCardsByUser(siteId)
            localRepo.saveAll(remoteCards)
            notificationUseCase(remove = true, syncCards = true)
        }
        return if (localCards) {
            localRepo.getAllLocal()
        } else {
            localRepo.getAll()
        }
    }
}
