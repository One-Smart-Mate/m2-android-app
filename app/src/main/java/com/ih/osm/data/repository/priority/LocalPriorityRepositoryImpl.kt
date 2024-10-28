package com.ih.osm.data.repository.priority

import com.ih.osm.data.database.dao.priority.PriorityDao
import com.ih.osm.data.database.entities.priority.toDomain
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.priority.LocalPriorityRepository
import javax.inject.Inject

class LocalPriorityRepositoryImpl @Inject constructor(
    private val dao: PriorityDao
) : LocalPriorityRepository {
    override suspend fun getAll(): List<Priority> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun saveAll(list: List<Priority>) {
        dao.deleteAll()
        list.forEach {
            dao.insert(it.toEntity())
        }
    }

    override suspend fun get(id: String): Priority? {
        return dao.get(id)?.toDomain()
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
