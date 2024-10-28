package com.ih.osm.domain.repository.cards

import com.ih.osm.domain.model.Card

interface LocalCardRepository {
    suspend fun saveAll(cards: List<Card>)
    suspend fun getAll(): List<Card>
    suspend fun getLastCardId(): String?
    suspend fun getLastSiteCardId(): Long?
    suspend fun save(card: Card): Long
    suspend fun getAllLocal(): List<Card>
    suspend fun delete(uuid: String)
    suspend fun get(uuid: String): Card
    suspend fun getByZone(siteId: String, superiorId: String): List<Card>
    suspend fun deleteAll()
}
