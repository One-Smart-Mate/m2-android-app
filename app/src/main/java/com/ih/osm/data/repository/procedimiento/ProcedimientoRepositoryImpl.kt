package com.ih.osm.data.repository.procedimiento

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.model.ProcedimientoCiltData
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.procedimiento.ProcedimientoRepository
import javax.inject.Inject

class ProcedimientoRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) : ProcedimientoRepository {
        override suspend fun getRemoteByLevel(levelId: String): ProcedimientoCiltData {
            return networkRepository.getRemoteProcedimientoCiltsByLevel(levelId)
        }

        override suspend fun getByLevel(levelId: String): ProcedimientoCiltData {
            // For now, we return mock data for local
            // It can be implemented later with a local DAO if necessary
            return ProcedimientoCiltData.mockData()
        }

        override suspend fun createExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse {
            return networkRepository.createCiltExecution(request)
        }
    }
