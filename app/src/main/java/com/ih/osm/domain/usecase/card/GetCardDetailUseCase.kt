package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.isRemoteCard
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.ui.extensions.defaultIfNull
import javax.inject.Inject

interface GetCardDetailUseCase {
    suspend operator fun invoke(uuid: String, remote: Boolean = true): Card?
}

class GetCardDetailUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val localRepo: LocalCardRepository
) : GetCardDetailUseCase {
    override suspend fun invoke(uuid: String, remote: Boolean): Card? {
        val card = localRepo.get(uuid)
        card?.let {
            return if (card.isRemoteCard() && remote && NetworkConnection.isConnected()) {
                remoteRepo.getCardDetail(card.id)
                    .defaultIfNull(localRepo.get(uuid))
            } else {
                card
            }
        } ?: return null
    }
}
