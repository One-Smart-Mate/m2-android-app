package com.osm.data.repository.cards

import com.osm.data.api.ApiService
import com.osm.data.model.CreateCardRequest
import com.osm.data.model.CreateDefinitiveSolutionRequest
import com.osm.data.model.CreateProvisionalSolutionRequest
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.Card
import com.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CardRepository {

    override suspend fun getCardsByUser(siteId: String): List<Card> {
        val response = apiService.getCards(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun getCardDetail(cardId: String): Card? {
        val response = apiService.getCardDetail(cardId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun saveCard(card: CreateCardRequest): Card {
        val response = apiService.createCard(card).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun getCardsZone(superiorId: String, siteId: String): List<Card> {
        val response = apiService.getCardsZone(superiorId, siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun saveDefinitiveSolution(createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest): Card {
        val response = apiService.saveDefinitiveSolution(createDefinitiveSolutionRequest).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun saveProvisionalSolution(createProvisionalSolutionRequest: CreateProvisionalSolutionRequest): Card {
        val response =
            apiService.saveProvisionalSolution(createProvisionalSolutionRequest).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun getCardsLevelMachine(levelMachine: String, siteId: String): List<Card> {
        val response = apiService.getCardsLevelMachine(siteId, levelMachine).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}

