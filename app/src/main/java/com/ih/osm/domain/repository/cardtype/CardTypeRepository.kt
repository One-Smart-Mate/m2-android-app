package com.ih.osm.domain.repository.cardtype

import com.ih.osm.domain.model.CardType

interface CardTypeRepository {
    suspend fun getCardTypes(siteId: String): List<CardType>
}
