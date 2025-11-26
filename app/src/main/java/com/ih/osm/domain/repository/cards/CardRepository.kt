package com.ih.osm.domain.repository.cards

import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.domain.model.Card

interface CardRepository {
    suspend fun getAllRemoteByUser(): List<Card>

    /**
     * Get all cards for the current user with pagination support
     * @param page Page number (optional)
     * @param limit Items per page (optional)
     * @return List of cards for the current page
     */
    suspend fun getAllRemoteByUser(
        page: Int? = null,
        limit: Int? = null,
    ): List<Card>

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

    /**
     * Get cards by level ID from remote with optional pagination
     * @param levelId The level ID
     * @param page Page number (optional)
     * @param limit Items per page (optional)
     * @return List of cards for the specified level
     */
    suspend fun getRemoteByLevel(
        levelId: String,
        page: Int? = null,
        limit: Int? = null,
    ): List<Card>
}
