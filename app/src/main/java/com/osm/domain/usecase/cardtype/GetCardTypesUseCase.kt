package com.osm.domain.usecase.cardtype

import com.osm.domain.model.CardType
import com.osm.domain.repository.cardtype.CardTypeRepository
import com.osm.domain.repository.local.LocalRepository
import com.osm.ui.utils.EMPTY
import javax.inject.Inject

interface GetCardTypesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false, filter: String = EMPTY): List<CardType>
}

class GetCardTypesUseCaseImpl @Inject constructor(
    private val cardTypeRepository: CardTypeRepository,
    private val localRepository: LocalRepository
) : GetCardTypesUseCase {
    override suspend fun invoke(syncRemote: Boolean, filter: String): List<CardType> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val cardTypes = cardTypeRepository.getCardTypes(siteId)
            localRepository.saveCardTypes(cardTypes)
        }
        return localRepository.getCardTypes(filter)
    }
}