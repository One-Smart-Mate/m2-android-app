package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.utils.EMPTY
import javax.inject.Inject

interface GetCardsZoneUseCase {
    suspend operator fun invoke(
        superiorId: String = EMPTY,
    ): List<Card>
}

class GetCardsZoneUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardsZoneUseCase {

    override suspend fun invoke(superiorId: String): List<Card> {
        return if (NetworkConnection.isConnected()) {
            val siteId = localRepository.getSiteId()
            cardRepository.getCardsZone(siteId)
        } else {
            localRepository.getCardsZone(superiorId)
        }
    }
}