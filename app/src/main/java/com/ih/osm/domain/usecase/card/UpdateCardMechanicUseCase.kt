package com.ih.osm.domain.usecase.card

import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface UpdateCardMechanicUseCase {
    suspend operator fun invoke(mechanicId: String, uuid: String): Card
}

class UpdateCardMechanicUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val localRepo: LocalCardRepository,
    private val appLocalRepository: LocalRepository
) : UpdateCardMechanicUseCase {
    override suspend fun invoke(mechanicId: String, uuid: String): Card {
        val userId = appLocalRepository.getUser()?.userId.orEmpty().toInt()
        val card = localRepo.get(uuid) ?: error("Card $uuid not found")
        val request = UpdateMechanicRequest(card.id.toInt(), mechanicId.toInt(), userId)
        remoteRepo.updateMechanic(request)
        val employee = appLocalRepository.getEmployees().firstOrNull { it.id == mechanicId }
        val newCard = card.copy(
            mechanicId = employee?.id,
            mechanicName = employee?.name
        )
        localRepo.save(newCard)
        return newCard
    }
}
