package com.ih.m2.domain.usecase.card

import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import javax.inject.Inject

interface CreateCardUseCase {
    suspend operator fun invoke(card: CreateCardRequest): Card
}

class CreateCardUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
) : CreateCardUseCase {

    override suspend fun invoke(card: CreateCardRequest): Card {
        return cardRepository.createCard(card)
    }
}