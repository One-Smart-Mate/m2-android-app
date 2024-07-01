package com.ih.m2.data.repository.cards

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.ui.functions.customError
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.data.model.CreateDefinitiveSolutionRequest
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
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

    override suspend fun getCardDetail(cardId: String): Card {
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

    override suspend fun getCardsZone(siteId: String): List<Card> {
        val response = apiService.getCardsZone(siteId).execute()
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
}

