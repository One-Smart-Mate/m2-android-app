package com.ih.osm.domain.usecase.procedure

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.repository.procedure.ProcedureRepository
import javax.inject.Inject

interface GetPositionProceduresUseCase {
    suspend operator fun invoke(): CiltProcedureData
}

class GetPositionProceduresUseCaseImpl
    @Inject
    constructor(
        private val repo: ProcedureRepository,
    ) : GetPositionProceduresUseCase {
        override suspend fun invoke(): CiltProcedureData =
            if (NetworkConnection.isConnected()) {
                repo.getRemotePositionProcedures()
            } else {
                // If offline, return mock data or throw exception
                CiltProcedureData.mockData()
            }
    }
