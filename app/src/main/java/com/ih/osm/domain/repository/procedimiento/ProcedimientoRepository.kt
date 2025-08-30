package com.ih.osm.domain.repository.procedimiento

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.model.ProcedimientoCiltData

interface ProcedimientoRepository {
    suspend fun getByLevel(levelId: String): ProcedimientoCiltData

    suspend fun getRemoteByLevel(levelId: String): ProcedimientoCiltData

    suspend fun createExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse
}
