package com.ih.osm.domain.usecase.card

import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import javax.inject.Inject

interface UpdateCardMechanicUseCase {
    suspend operator fun invoke(mechanicId: String, uuid: String): Card
}

class UpdateCardMechanicUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val localRepo: LocalCardRepository,
    private val authRepo: AuthRepository,
    private val employeeRepo: EmployeeRepository
) : UpdateCardMechanicUseCase {
    override suspend fun invoke(mechanicId: String, uuid: String): Card {
        val userId = authRepo.get()?.userId.orEmpty().toInt()
        val card = localRepo.get(uuid) ?: error("Card $uuid not found")
        val request = UpdateMechanicRequest(card.id.toInt(), mechanicId.toInt(), userId)
        remoteRepo.updateMechanic(request)
        val employee = employeeRepo.getAll().firstOrNull { it.id == mechanicId }
        val newCard = card.copy(
            mechanicId = employee?.id,
            mechanicName = employee?.name
        )
        localRepo.save(newCard)
        return newCard
    }
}
