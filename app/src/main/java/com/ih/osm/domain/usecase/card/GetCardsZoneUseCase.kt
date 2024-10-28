package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface GetCardsZoneUseCase {
    suspend operator fun invoke(superiorId: String): List<Card>
}

class GetCardsZoneUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val authRepository: AuthRepository,
    private val localRepo: LocalCardRepository,
    private val levelRepo: LevelRepository
) : GetCardsZoneUseCase {
    override suspend fun invoke(superiorId: String): List<Card> {
        val siteId = authRepository.getSiteId()
        val area = levelRepo.get(superiorId)
        val id = area?.superiorId.orEmpty()
        return if (NetworkConnection.isConnected()) {
            remoteRepo.getCardsZone(superiorId = id, siteId = siteId)
        } else {
            localRepo.getByZone(superiorId = id, siteId = siteId)
        }
    }
}
