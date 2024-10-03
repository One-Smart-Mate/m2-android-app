package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.isRemoteCard
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.ui.extensions.defaultIfNull
import javax.inject.Inject

interface GetCardDetailUseCase {
    suspend operator fun invoke(cardId: String, remote: Boolean = true): Card
}

class GetCardDetailUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : GetCardDetailUseCase {
    override suspend fun invoke(cardId: String, remote: Boolean): Card {
        val card = localRepository.getCard(cardId)
        return if (card.isRemoteCard() && remote && NetworkConnection.isConnected()) {
            cardRepository.getCardDetail(cardId)
                .defaultIfNull(localRepository.getCard(cardId))
        } else {
            card
        }
    }
}
