package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardsLevelMachineUseCase {
    suspend operator fun invoke(levelMachine: String): List<Card>
}

class GetCardsLevelMachineUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardsLevelMachineUseCase {
    override suspend fun invoke(levelMachine: String): List<Card> {
        val siteId = localRepository.getSiteId()
        return if (NetworkConnection.isConnected()) {
            cardRepository.getCardsLevelMachine(levelMachine = levelMachine, siteId = siteId)
        } else {
            emptyList()
        }
    }
}
