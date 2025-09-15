package com.ih.osm.data.repository.solution

import com.ih.osm.data.database.dao.solution.SolutionDao
import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

class SolutionRepositoryImpl
    @Inject
    constructor(
        private val dao: SolutionDao,
        private val networkRepository: NetworkRepository,
    ) : SolutionRepository {
        override suspend fun save(solutionEntity: SolutionEntity) {
            dao.insert(solutionEntity)
        }

        override suspend fun deleteAll() {
            dao.deleteAll()
        }

        override suspend fun getAllByCard(uuid: String): List<SolutionEntity> = dao.getAllByCard(uuid)

        override suspend fun deleteAllByCard(uuid: String) {
            dao.deleteAllByCard(uuid)
        }

        override suspend fun getAll(): List<SolutionEntity> = dao.getAll()

        override suspend fun saveRemoteDefinitive(body: CreateDefinitiveSolutionRequest): Card =
            networkRepository.saveRemoteDefinitiveSolution(body)

        override suspend fun saveRemoteProvisional(body: CreateProvisionalSolutionRequest): Card =
            networkRepository.saveRemoteProvisionalSolution(body)
    }
