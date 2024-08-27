package com.ih.m2.domain.repository.cards

import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.data.model.CreateDefinitiveSolutionRequest
import com.ih.m2.data.model.CreateProvisionalSolutionRequest
import com.ih.m2.domain.model.Card

interface CardRepository {

    suspend fun getCardsByUser(siteId: String): List<Card>

    suspend fun getCardDetail(cardId: String): Card?

    suspend fun saveCard(card: CreateCardRequest): Card


    suspend fun getCardsZone(superiorId: String,siteId: String): List<Card>


    suspend fun saveDefinitiveSolution(createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest): Card

    suspend fun saveProvisionalSolution(createProvisionalSolutionRequest: CreateProvisionalSolutionRequest): Card

    suspend fun getCardsLevelMachine(levelMachine: String, siteId: String): List<Card>
}