package com.ih.osm.data.repository.level

import com.ih.osm.data.database.dao.level.LevelDao
import com.ih.osm.data.database.entities.level.toDomain
import com.ih.osm.data.model.GetPaginatedLevelsResponse
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
        private val authRepository: AuthRepository,
    ) : LevelRepository {
        override suspend fun saveAll(list: List<Level>) {
            list.forEach {
                dao.insert(it.toEntity())
            }
        }

        override suspend fun getAll(): List<Level> = dao.getAll().map { it.toDomain() }

        override suspend fun deleteAll() {
            dao.deleteAll()
        }

        override suspend fun get(id: String): Level? = dao.get(id)?.toDomain()

        override suspend fun getAllRemote(
            page: Int,
            limit: Int,
        ): GetPaginatedLevelsResponse {
            val siteId = authRepository.getSiteId()
            return networkRepository.getRemoteLevels(siteId, page, limit)
        }

//        override suspend fun getRemoteLevelsWithLocation(
//            page: Int?,
//            limit: Int?,
//        ): List<Level> {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.getRemoteLevelsWithLocation(siteId, page, limit)
//        }
//
//        override suspend fun getRemoteSiteLevels(
//            page: Int?,
//            limit: Int?,
//        ): List<Level> {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.getRemoteSiteLevels(siteId, page, limit)
//        }
//
//        override suspend fun getRemoteLevelTreeLazy(
//            page: Int?,
//            limit: Int?,
//            depth: Int?,
//        ): LevelTreeData {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.getRemoteLevelTreeLazy(siteId, page, limit, depth)
//        }
//
//        override suspend fun getRemoteChildrenLevels(
//            parentId: String,
//            page: Int?,
//            limit: Int?,
//        ): List<Level> {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.getRemoteChildrenLevels(siteId, parentId, page, limit)
//        }
//
//        override suspend fun getRemoteLevelStats(
//            page: Int?,
//            limit: Int?,
//        ): LevelStats {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.getRemoteLevelStats(siteId, page, limit)
//        }
//
//        override suspend fun findByMachineId(machineId: String): List<Level> {
//            val siteId = authRepository.getSiteId()
//            return networkRepository.findLevelByMachineId(siteId, machineId)
//        }
    }
