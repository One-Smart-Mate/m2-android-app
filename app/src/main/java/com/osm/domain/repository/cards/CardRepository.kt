package com.osm.domain.repository.cards

import com.osm.data.model.CreateCardRequest
import com.osm.data.model.CreateDefinitiveSolutionRequest
import com.osm.data.model.CreateProvisionalSolutionRequest
import com.osm.domain.model.Card

interface CardRepository {

    suspend fun getCardsByUser(siteId: String): List<Card>

    suspend fun getCardDetail(cardId: String): Card?

    suspend fun saveCard(card: CreateCardRequest): Card


    suspend fun getCardsZone(superiorId: String,siteId: String): List<Card>


    suspend fun saveDefinitiveSolution(createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest): Card

    suspend fun saveProvisionalSolution(createProvisionalSolutionRequest: CreateProvisionalSolutionRequest): Card

    suspend fun getCardsLevelMachine(levelMachine: String, siteId: String): List<Card>
}