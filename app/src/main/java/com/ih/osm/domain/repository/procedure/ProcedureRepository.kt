package com.ih.osm.domain.repository.procedure

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.model.CiltProcedureData

interface ProcedureRepository {
    suspend fun getByLevel(levelId: String): CiltProcedureData

    suspend fun getRemoteByLevel(levelId: String): CiltProcedureData

    suspend fun createExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse
}
