package com.ih.m2.domain.usecase.card

import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardsLevelMachineUseCase {
    suspend operator fun invoke(
        levelMachine: String,
    ): List<Card>
}

class GetCardsLevelMachineUseCaseImpl @Inject constructor(
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