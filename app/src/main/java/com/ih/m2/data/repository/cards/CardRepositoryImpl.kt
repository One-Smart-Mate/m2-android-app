package com.ih.m2.data.repository.cards

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.cards.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): CardRepository {

    override suspend fun getCardsByUser(siteId: String): List<Card> {
        val response = apiService.getCards(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            FirebaseCrashlytics.getInstance().log(response.getErrorMessage())
            error(response.getErrorMessage())
        }
    }

    override suspend fun getCardDetail(cardId: String): Card {
        val response = apiService.getCardDetail(cardId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            FirebaseCrashlytics.getInstance().log(response.getErrorMessage())
            error(response.getErrorMessage())
        }
    }
}