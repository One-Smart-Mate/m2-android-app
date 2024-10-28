package com.ih.osm.domain.repository.solution

import com.ih.osm.data.database.entities.solution.SolutionEntity

interface SolutionRepository {

    suspend fun save(solutionEntity: SolutionEntity)

    suspend fun deleteAll()

    suspend fun getAllByCard(uuid: String): List<SolutionEntity>

    suspend fun deleteAllByCard(uuid: String)

    suspend fun getAll(): List<SolutionEntity>
}
