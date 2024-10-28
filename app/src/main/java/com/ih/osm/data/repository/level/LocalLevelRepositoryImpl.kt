package com.ih.osm.data.repository.level

import com.ih.osm.data.database.dao.level.LevelDao
import com.ih.osm.data.database.entities.level.toDomain
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.level.LocalLevelRepository
import javax.inject.Inject

class LocalLevelRepositoryImpl @Inject constructor(
    private val dao: LevelDao
) : LocalLevelRepository {

    override suspend fun saveAll(list: List<Level>) {
        dao.deleteAll()
        list.forEach {
            dao.insert(it.toEntity())
        }
    }

    override suspend fun getAll(): List<Level> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun get(id: String): Level? {
        return dao.get(id)?.toDomain()
    }
}
