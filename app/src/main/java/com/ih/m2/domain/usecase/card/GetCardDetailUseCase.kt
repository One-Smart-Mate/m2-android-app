package com.ih.m2.domain.usecase.card

import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.extensions.defaultIfNull
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