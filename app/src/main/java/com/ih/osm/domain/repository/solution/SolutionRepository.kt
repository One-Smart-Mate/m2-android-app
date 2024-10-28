package com.ih.osm.domain.repository.solution

import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.domain.model.Card

interface SolutionRepository {

    suspend fun save(solutionEntity: SolutionEntity)

    suspend fun deleteAll()

    suspend fun getAllByCard(uuid: String): List<SolutionEntity>

    suspend fun deleteAllByCard(uuid: String)

    suspend fun getAll(): List<SolutionEntity>

    suspend fun saveRemoteDefinitive(body: CreateDefinitiveSolutionRequest): Card

    suspend fun saveRemoteProvisional(body: CreateProvisionalSolutionRequest): Card
}
