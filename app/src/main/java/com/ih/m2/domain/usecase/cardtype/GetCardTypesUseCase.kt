package com.ih.m2.domain.usecase.cardtype

import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardTypesUseCase {
    suspend operator fun invoke(): List<CardType>
}

class GetCardTypesUseCaseImpl @Inject constructor(
    private val cardTypeRepository: CardTypeRepository,
    private val localRepository: LocalRepository
) : GetCardTypesUseCase {
    override suspend fun invoke(): List<CardType> {
        val siteId = localRepository.getSiteId()
        return cardTypeRepository.getCardTypes(siteId)
    }
}