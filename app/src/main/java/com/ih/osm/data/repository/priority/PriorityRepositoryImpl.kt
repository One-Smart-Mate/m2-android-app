package com.ih.osm.data.repository.priority

import com.ih.osm.data.database.dao.priority.PriorityDao
import com.ih.osm.data.database.entities.priority.toDomain
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
import javax.inject.Inject

class PriorityRepositoryImpl
@Inject
constructor(
    private val networkRepository: NetworkRepository,
    private val dao: PriorityDao,
    private val authRepository: AuthRepository
) : PriorityRepository {
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

    override suspend fun getAllRemote(): List<Priority> {
        val siteId = authRepository.getSiteId()
        return networkRepository.getRemotePriorities(siteId)
    }
}
