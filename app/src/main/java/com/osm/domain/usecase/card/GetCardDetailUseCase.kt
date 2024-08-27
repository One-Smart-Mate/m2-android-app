package com.osm.domain.usecase.card

import com.osm.core.network.NetworkConnection
import com.osm.domain.model.Card
import com.osm.domain.repository.cards.CardRepository
import com.osm.domain.repository.local.LocalRepository
import com.osm.ui.extensions.defaultIfNull
import javax.inject.Inject

interface GetCardDetailUseCase {
    suspend operator fun invoke(cardId: String, remote: Boolean = true): Card
}

class GetCardDetailUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardDetailUseCase {

    override suspend fun invoke(cardId: String, remote: Boolean): Card {
        return if (remote && NetworkConnection.isConnected()) {
            cardRepository.getCardDetail(cardId)
                .defaultIfNull(localRepository.getCard(cardId))
        } else {
            localRepository.getCard(cardId)
        }
    }
}