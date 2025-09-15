package com.ih.osm.domain.usecase.procedure

import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.domain.repository.procedure.ProcedureRepository
import javax.inject.Inject

interface CreateCiltExecutionUseCase {
    suspend operator fun invoke(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse
}

class CreateCiltExecutionUseCaseImpl
    @Inject
    constructor(
        private val repo: ProcedureRepository,
    ) : CreateCiltExecutionUseCase {
        override suspend fun invoke(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse = repo.createExecution(request)
    }
