package com.ih.osm.data.repository.cardtype

import com.ih.osm.data.database.dao.cardtype.CardTypeDao
import com.ih.osm.data.database.entities.cardtype.toDomain
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

class CardTypeRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
        private val dao: CardTypeDao,
        private val authRepository: AuthRepository,
    ) : CardTypeRepository {
        override suspend fun getAll(filter: String): List<CardType> {
            return if (filter.isEmpty()) {
                dao.getAll().map { it.toDomain() }
            } else {
                dao.getByMethodology(filter).map { it.toDomain() }
            }
        }

        override suspend fun saveAll(list: List<CardType>) {
            dao.deleteAll()
            list.forEach {
                dao.insert(it.toEntity())
            }
        }

        override suspend fun get(id: String): CardType? {
            return dao.get(id)?.toDomain()
        }

        override suspend fun deleteAll() {
            dao.deleteAll()
        }

        override suspend fun getAllRemote(): List<CardType> {
            val siteId = authRepository.getSiteId()
            return networkRepository.getRemoteCardTypes(siteId)
        }
    }
