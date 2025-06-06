package com.ih.osm.domain.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Opl

interface CiltRepository {
    suspend fun getCilts(body: GetCiltsRequest): CiltData

    suspend fun startSequenceExecution(body: StartSequenceExecutionRequest)

    suspend fun stopSequenceExecution(body: StopSequenceExecutionRequest)

    suspend fun createEvidence(body: CiltEvidenceRequest)

    suspend fun getOplById(id: String): Opl
}
