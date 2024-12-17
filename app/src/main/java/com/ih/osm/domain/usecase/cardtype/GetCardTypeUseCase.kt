package com.ih.osm.domain.usecase.cardtype

import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import javax.inject.Inject

interface GetCardTypeUseCase {
    suspend operator fun invoke(id: String): CardType?
}

class GetCardTypeUseCaseImpl
    @Inject
    constructor(
        private val repo: CardTypeRepository,
    ) : GetCardTypeUseCase {
        override suspend fun invoke(id: String): CardType? {
            return repo.get(id)
        }
    }
