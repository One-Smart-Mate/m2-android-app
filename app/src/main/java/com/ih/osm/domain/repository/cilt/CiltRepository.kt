package com.ih.osm.domain.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.SequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.SequenceExecutionData

interface CiltRepository {
    suspend fun getCilts(body: GetCiltsRequest): CiltData

    suspend fun updateSequenceExecution(body: SequenceExecutionRequest): SequenceExecutionData

    suspend fun createEvidence(body: CiltEvidenceRequest)
}
