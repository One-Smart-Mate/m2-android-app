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
        override suspend fun getRemoteByLevel(levelId: String): List<Opl> {
            return networkRepository.getRemoteOplsByLevel(levelId)
        }

        override suspend fun getByLevel(levelId: String): List<Opl> {
            // Por ahora retornamos lista vacía para datos locales
            // Se puede implementar posteriormente con DAO local si es necesario
            return emptyList()
        }

        override suspend fun saveAll(opls: List<Opl>) {
            // Se puede implementar posteriormente con DAO local si es necesario
            try {
                // dao.insertAll(opls.map { it.toEntity() })
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
            }
        }

        override suspend fun getAll(): List<Opl> {
            // Se puede implementar posteriormente con DAO local si es necesario
            return emptyList()
        }

        override suspend fun deleteAll() {
            // Se puede implementar posteriormente con DAO local si es necesario
        }
    }
