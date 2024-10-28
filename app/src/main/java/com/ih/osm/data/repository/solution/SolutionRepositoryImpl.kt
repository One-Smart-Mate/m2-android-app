package com.ih.osm.data.repository.solution

import com.ih.osm.data.database.dao.solution.SolutionDao
import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

class SolutionRepositoryImpl @Inject constructor(
    private val dao: SolutionDao
) : SolutionRepository {
    override suspend fun save(solutionEntity: SolutionEntity) {
        dao.insert(solutionEntity)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun getAllByCard(uuid: String): List<SolutionEntity> {
        return dao.getAllByCard(uuid)
    }

    override suspend fun deleteAllByCard(uuid: String) {
        dao.deleteAllByCard(uuid)
    }

    override suspend fun getAll(): List<SolutionEntity> {
        return dao.getAll()
    }
}
