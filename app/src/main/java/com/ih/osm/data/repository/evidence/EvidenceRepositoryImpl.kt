package com.ih.osm.data.repository.evidence

import com.ih.osm.data.database.dao.evidence.EvidenceDao
import com.ih.osm.data.database.entities.evidence.toDomain
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import javax.inject.Inject

class EvidenceRepositoryImpl @Inject constructor(
    private val dao: EvidenceDao
) : EvidenceRepository {
    override suspend fun save(evidence: Evidence): Long {
        return dao.insert(evidence.toEntity())
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun deleteByCard(uuid: String) {
        dao.deleteByCard(uuid)
    }

    override suspend fun getAllByCard(uuid: String): List<Evidence> {
        return dao.getAllByCard(uuid).map { it.toDomain() }
    }
}
