package com.ih.m2.data.repository.cardtype

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import javax.inject.Inject

class CardTypeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): CardTypeRepository {

    override suspend fun getCardTypes(siteId: String): List<CardType> {
        val response = apiService.getCardTypes(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            FirebaseCrashlytics.getInstance().log(response.getErrorMessage())
            error(response.getErrorMessage())
        }
    }
}