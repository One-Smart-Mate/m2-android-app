package com.ih.osm.domain.usecase.card

import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface UpdateCardMechanicUseCase {
    suspend operator fun invoke(mechanicId: String, cardId: String): Card
}

class UpdateCardMechanicUseCaseImpl
@Inject
constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository
) : UpdateCardMechanicUseCase {
    override suspend fun invoke(mechanicId: String, cardId: String): Card {
        val userId = localRepository.getUser()?.userId.orEmpty().toInt()
        val request = UpdateMechanicRequest(cardId.toInt(), mechanicId.toInt(), userId)
        cardRepository.updateMechanic(request)
        val card = localRepository.getCard(cardId)
        val employee = localRepository.getEmployees().firstOrNull { it.id == mechanicId }
        val newCard = card.copy(
            mechanicId = employee?.id,
            mechanicName = employee?.name
        )
        localRepository.saveCard(newCard)
        return newCard
    }
}
