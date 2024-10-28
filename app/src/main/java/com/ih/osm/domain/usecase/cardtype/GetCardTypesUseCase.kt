package com.ih.osm.domain.usecase.cardtype

import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.cardtype.LocalCardTypeRepository
import com.ih.osm.ui.utils.EMPTY
import javax.inject.Inject

interface GetCardTypesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false, filter: String = EMPTY): List<CardType>
}

class GetCardTypesUseCaseImpl
@Inject
constructor(
    private val remoteRepository: CardTypeRepository,
    private val localRepository: LocalCardTypeRepository,
    private val authRepository: AuthRepository
) : GetCardTypesUseCase {
    override suspend fun invoke(syncRemote: Boolean, filter: String): List<CardType> {
        if (syncRemote) {
            val siteId = authRepository.getSiteId()
            val cardTypes = remoteRepository.getCardTypes(siteId)
            localRepository.saveAll(cardTypes)
        }
        return localRepository.getAll(filter)
    }
}
