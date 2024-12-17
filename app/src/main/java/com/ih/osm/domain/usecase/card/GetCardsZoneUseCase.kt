package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface GetCardsZoneUseCase {
    suspend operator fun invoke(superiorId: String): List<Card>
}

class GetCardsZoneUseCaseImpl
    @Inject
    constructor(
        private val repo: CardRepository,
        private val levelRepo: LevelRepository,
    ) : GetCardsZoneUseCase {
        override suspend fun invoke(superiorId: String): List<Card> {
            val area = levelRepo.get(superiorId)
            val id = area?.superiorId.orEmpty()
            return if (NetworkConnection.isConnected()) {
                repo.getRemoteByZone(superiorId = id)
            } else {
                repo.getByZone(superiorId = id)
            }
        }
    }
