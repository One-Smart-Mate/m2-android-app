package com.osm.data.repository.cardtype

import com.osm.data.api.ApiService
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.CardType
import com.osm.domain.repository.cardtype.CardTypeRepository
import javax.inject.Inject

class CardTypeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): CardTypeRepository {

    override suspend fun getCardTypes(siteId: String): List<CardType> {
        val response = apiService.getCardTypes(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}