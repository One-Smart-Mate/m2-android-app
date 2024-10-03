package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardsZoneUseCase {
    suspend operator fun invoke(superiorId: String): List<Card>
}

class GetCardsZoneUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardsZoneUseCase {
    override suspend fun invoke(superiorId: String): List<Card> {
        val siteId = localRepository.getSiteId()
        val area = localRepository.getLevel(superiorId)
        val id = area?.superiorId.orEmpty()
        return if (NetworkConnection.isConnected()) {
            cardRepository.getCardsZone(superiorId = id, siteId = siteId)
        } else {
            localRepository.getCardsZone(superiorId = id, siteId = siteId)
        }
    }
}
