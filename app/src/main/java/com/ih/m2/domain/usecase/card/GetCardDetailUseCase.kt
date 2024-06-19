package com.ih.m2.domain.usecase.card

import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import javax.inject.Inject

interface GetCardDetailUseCase {
    suspend operator fun invoke(cardId: String): Card
}

class GetCardDetailUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
) : GetCardDetailUseCase {

    override suspend fun invoke(cardId: String): Card {
        return cardRepository.getCardDetail(cardId)
    }
}