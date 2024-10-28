package com.ih.osm.domain.repository.evidence

import com.ih.osm.domain.model.Evidence

interface EvidenceRepository {
    suspend fun save(evidence: Evidence): Long

    suspend fun delete(id: String)

    suspend fun deleteAll()

    suspend fun deleteByCard(uuid: String)

    suspend fun getAllByCard(uuid: String): List<Evidence>
}
