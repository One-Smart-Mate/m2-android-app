package com.ih.osm.domain.repository.local

import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.domain.model.User

interface LocalRepository {
    suspend fun saveUser(user: User): Long

    suspend fun getUser(): User?

    suspend fun logout(): Int

    suspend fun getSiteId(): String

    suspend fun saveSolution(solutionEntity: SolutionEntity)

    suspend fun removeSolutions()

    suspend fun getCardSolutions(cardId: String): List<SolutionEntity>

    suspend fun deleteSolutions(cardId: String)
}
