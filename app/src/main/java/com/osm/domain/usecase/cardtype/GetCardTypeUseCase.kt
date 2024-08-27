package com.osm.domain.usecase.cardtype

import com.osm.domain.model.CardType
import com.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetCardTypeUseCase {
    suspend operator fun invoke(id: String): CardType?
}

class GetCardTypeUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
) : GetCardTypeUseCase {
    override suspend fun invoke(id: String): CardType? {
        return localRepository.getCardType(id)
    }
}