package com.ih.osm.domain.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.SequenceExecution

interface CiltRepository {
    suspend fun getCilts(date: String): CiltData

    suspend fun startSequenceExecution(body: StartSequenceExecutionRequest): SequenceExecution

    suspend fun stopSequenceExecution(body: StopSequenceExecutionRequest): SequenceExecution

    suspend fun createEvidence(body: CiltEvidenceRequest): CiltSequenceEvidence

    suspend fun getOplById(id: String): Opl

    suspend fun getSequence(id: Int): Sequence
}
