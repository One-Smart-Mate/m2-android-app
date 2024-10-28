package com.ih.osm.domain.repository.cardtype

import com.ih.osm.domain.model.CardType
import com.ih.osm.ui.utils.EMPTY

interface LocalCardTypeRepository {

    suspend fun getAll(filter: String = EMPTY): List<CardType>

    suspend fun saveAll(list: List<CardType>)

    suspend fun get(id: String): CardType?

    suspend fun deleteAll()
}
