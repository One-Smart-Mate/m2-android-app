package com.ih.osm.domain.usecase.card

import android.util.Log
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
            Log.d("GetCardsUseCase", "===== EXECUTE: syncRemote=$syncRemote, localCards=$localCards =====")

            if (syncRemote && NetworkConnection.isConnected()) {
                Log.d("GetCardsUseCase", "Syncing from remote...")
                val remoteCards = repo.getAllRemoteByUser()
                Log.d("GetCardsUseCase", "Remote returned: ${remoteCards.size} cards")
                repo.saveAll(remoteCards)
                notificationUseCase(remove = true, syncCards = true)
            }

            val cards =
                if (localCards) {
                    Log.d("GetCardsUseCase", "Getting local cards...")
                    repo.getAllLocal()
                } else {
                    Log.d("GetCardsUseCase", "Getting all cards...")
                    repo.getAll()
                }

            Log.d("GetCardsUseCase", "SUCCESS: Returning ${cards.size} cards")
            return cards
        }
    }
