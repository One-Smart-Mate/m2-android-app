package com.ih.osm.domain.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.domain.model.CiltData

interface CiltRepository {
    suspend fun getCilts(body: GetCiltsRequest): CiltData

    suspend fun createEvidence(body: CiltEvidenceRequest)
}
