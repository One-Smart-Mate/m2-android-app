package com.ih.m2.domain.usecase.cardtype

import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardTypesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<CardType>
}

class GetCardTypesUseCaseImpl @Inject constructor(
    private val cardTypeRepository: CardTypeRepository,
    private val localRepository: LocalRepository
) : GetCardTypesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<CardType> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val cardTypes = cardTypeRepository.getCardTypes(siteId)
            localRepository.saveCardTypes(cardTypes)
        }
        return localRepository.getCardTypes()
    }
}