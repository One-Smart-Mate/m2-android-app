package com.ih.osm.domain.repository.cards

import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card

interface CardRepository {
    suspend fun getCardsByUser(siteId: String): List<Card>

    suspend fun getCardDetail(cardId: String): Card?

    suspend fun saveCard(card: CreateCardRequest): Card

    suspend fun getCardsZone(superiorId: String, siteId: String): List<Card>

    suspend fun saveDefinitiveSolution(
        createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest
    ): Card

    suspend fun saveProvisionalSolution(
        createProvisionalSolutionRequest: CreateProvisionalSolutionRequest
    ): Card

    suspend fun getCardsLevelMachine(levelMachine: String, siteId: String): List<Card>

    suspend fun updateMechanic(body: UpdateMechanicRequest)
}
