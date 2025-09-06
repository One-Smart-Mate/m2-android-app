package com.ih.osm.domain.usecase.procedure

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.repository.procedure.ProcedureRepository
import javax.inject.Inject

interface GetProcedureByLevelUseCase {
    suspend operator fun invoke(levelId: String): CiltProcedureData
}

class GetProcedureByLevelUseCaseImpl
    @Inject
    constructor(
        private val repo: ProcedureRepository,
    ) : GetProcedureByLevelUseCase {
        override suspend fun invoke(levelId: String): CiltProcedureData {
            return if (NetworkConnection.isConnected()) {
                repo.getRemoteByLevel(levelId = levelId)
            } else {
                repo.getByLevel(levelId = levelId)
            }
        }
    }
