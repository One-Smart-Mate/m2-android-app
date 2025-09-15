package com.ih.osm.data.repository.opl

import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.opl.OplRepository
import javax.inject.Inject

class OplRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) : OplRepository {
        override suspend fun getRemoteByLevel(levelId: String): List<Opl> = networkRepository.getRemoteOplsByLevel(levelId)

        override suspend fun getByLevel(levelId: String): List<Opl> {
            // For now, we return an empty list for local data
            // It can be implemented later with a local DAO if necessary
            return emptyList()
        }

        override suspend fun saveAll(opls: List<Opl>) {
            // It can be implemented later with a local DAO if necessary
            try {
                // dao.insertAll(opls.map { it.toEntity() })
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
            }
        }

        override suspend fun getAll(): List<Opl> {
            // It can be implemented later with a local DAO if necessary
            return emptyList()
        }

        override suspend fun deleteAll() {
            // It can be implemented later with a local DAO if necessary
        }
    }
