package com.ih.osm.data.repository.preclassifier

import com.ih.osm.data.database.dao.preclassifier.PreclassifierDao
import com.ih.osm.data.database.entities.preclassifier.toDomain
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.preclassifier.LocalPreclassifierRepository
import javax.inject.Inject

class LocalPreclassifierRepositoryImpl @Inject constructor(
    private val dao: PreclassifierDao
) : LocalPreclassifierRepository {
    override suspend fun getAll(): List<Preclassifier> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun saveAll(list: List<Preclassifier>) {
        dao.deleteAll()
        list.forEach {
            dao.insert(it.toEntity())
        }
    }

    override suspend fun get(id: String): Preclassifier? {
        return dao.get(id)?.toDomain()
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
