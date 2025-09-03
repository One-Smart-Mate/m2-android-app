package com.ih.osm.domain.usecase.procedimiento

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.repository.procedimiento.ProcedimientoRepository
import javax.inject.Inject

interface CreateCiltExecutionUseCase {
    suspend operator fun invoke(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse
}

class CreateCiltExecutionUseCaseImpl
    @Inject
    constructor(
        private val repo: ProcedimientoRepository,
    ) : CreateCiltExecutionUseCase {
        override suspend fun invoke(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse {
            return repo.createExecution(request)
        }
    }
