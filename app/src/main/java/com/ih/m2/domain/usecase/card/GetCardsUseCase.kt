package com.ih.m2.domain.usecase.card

import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardsUseCase  {
    suspend operator fun invoke(): List<Card>
}

class GetCardsUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
): GetCardsUseCase {

    override suspend fun invoke(): List<Card> {
        val user = localRepository.getUser()
        return user?.let {
            cardRepository.getCardsByUser(it.siteId)
        } ?: emptyList()
    }
}