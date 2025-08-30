package com.ih.osm.domain.usecase.procedimiento

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.ProcedimientoCiltData
import com.ih.osm.domain.repository.procedimiento.ProcedimientoRepository
import javax.inject.Inject

interface GetProcedimientosByLevelUseCase {
    suspend operator fun invoke(levelId: String): ProcedimientoCiltData
}

class GetProcedimientosByLevelUseCaseImpl
    @Inject
    constructor(
        private val repo: ProcedimientoRepository,
    ) : GetProcedimientosByLevelUseCase {
        override suspend fun invoke(levelId: String): ProcedimientoCiltData {
            return if (NetworkConnection.isConnected()) {
                repo.getRemoteByLevel(levelId = levelId)
            } else {
                repo.getByLevel(levelId = levelId)
            }
        }
    }
