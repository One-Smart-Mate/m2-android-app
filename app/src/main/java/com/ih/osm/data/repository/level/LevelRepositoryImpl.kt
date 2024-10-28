package com.ih.osm.data.repository.level

import com.ih.osm.data.database.dao.level.LevelDao
import com.ih.osm.data.database.entities.level.toDomain
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

class LevelRepositoryImpl
@Inject
constructor(
    private val dao: LevelDao,
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository
) : LevelRepository {
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

    override suspend fun getAllRemote(): List<Level> {
        val siteId = authRepository.getSiteId()
        return networkRepository.getRemoteLevels(siteId)
    }
}
