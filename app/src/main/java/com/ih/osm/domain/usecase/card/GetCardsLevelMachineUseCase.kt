package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

interface GetCardsLevelMachineUseCase {
    suspend operator fun invoke(levelMachine: String): List<Card>
}

class GetCardsLevelMachineUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository
) : GetCardsLevelMachineUseCase {
    override suspend fun invoke(levelMachine: String): List<Card> {
        return if (NetworkConnection.isConnected()) {
            cardRepository.getRemoteByLevelMachine(levelMachine = levelMachine)
        } else {
            emptyList()
        }
    }
}
