package com.ih.m2.domain.usecase.cardtype

import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardTypeUseCase {
    suspend operator fun invoke(id: String): CardType
}

class GetCardTypeUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
) : GetCardTypeUseCase {
    override suspend fun invoke(id: String): CardType {
        return localRepository.getCardType(id)
    }
}