package com.osm.domain.repository.cardtype

import com.osm.domain.model.CardType

interface CardTypeRepository {

    suspend fun getCardTypes(siteId: String): List<CardType>
}