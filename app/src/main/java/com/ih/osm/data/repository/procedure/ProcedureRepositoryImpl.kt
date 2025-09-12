package com.ih.osm.data.repository.procedure

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.procedure.ProcedureRepository
import javax.inject.Inject

class ProcedureRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) : ProcedureRepository {
        override suspend fun getRemoteByLevel(levelId: String): CiltProcedureData {
            return networkRepository.getRemoteCiltProcedureByLevel(levelId)
        }

        override suspend fun getByLevel(levelId: String): CiltProcedureData {
            // For now, we return mock data for local
            // It can be implemented later with a local DAO if necessary
            return CiltProcedureData.mockData()
        }

        override suspend fun createExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse {
            return networkRepository.createCiltExecution(request)
        }
    }
