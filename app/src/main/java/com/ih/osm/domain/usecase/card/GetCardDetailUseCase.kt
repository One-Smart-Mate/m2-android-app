package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.isRemoteCard
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.ui.extensions.defaultIfNull
import javax.inject.Inject

interface GetCardDetailUseCase {
    suspend operator fun invoke(
        uuid: String,
        remote: Boolean = true,
    ): Card
}

class GetCardDetailUseCaseImpl
    @Inject
    constructor(
        private val repo: CardRepository,
    ) : GetCardDetailUseCase {
        override suspend fun invoke(
            uuid: String,
            remote: Boolean,
        ): Card {
            val card = repo.get(uuid)!!
            return if (card.isRemoteCard() && remote && NetworkConnection.isConnected()) {
                repo.getRemote(card.id).defaultIfNull(card)
            } else {
                card
            }
        }
    }
