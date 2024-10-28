package com.ih.osm.domain.repository.cards

import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card

interface CardRepository {
    suspend fun getAllRemoteByUser(): List<Card>
    suspend fun getRemote(cardId: String): Card?
    suspend fun saveRemote(card: CreateCardRequest): Card
    suspend fun getRemoteByZone(superiorId: String): List<Card>
    suspend fun getRemoteByLevelMachine(levelMachine: String): List<Card>
    suspend fun updateRemoteMechanic(body: UpdateMechanicRequest)

    suspend fun saveAll(cards: List<Card>)
    suspend fun getAll(): List<Card>
    suspend fun getLastCardId(): String?
    suspend fun getLastSiteCardId(): Long?
    suspend fun save(card: Card): Long
    suspend fun getAllLocal(): List<Card>
    suspend fun delete(uuid: String)
    suspend fun get(uuid: String): Card?
    suspend fun getByZone(superiorId: String): List<Card>
    suspend fun deleteAll()
}
