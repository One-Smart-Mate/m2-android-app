package com.ih.osm.data.repository.cardtype

import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.repository.network.getErrorMessage
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import javax.inject.Inject

class CardTypeRepositoryImpl
@Inject
constructor(
    private val apiService: ApiService
) : CardTypeRepository {
    override suspend fun getCardTypes(siteId: String): List<CardType> {
        val response = apiService.getCardTypes(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}
