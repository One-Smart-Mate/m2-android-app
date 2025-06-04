package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.SequenceExecutionRequest
import com.ih.osm.domain.model.SequenceExecutionData
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface UpdateSequenceExecutionUseCase {
    suspend operator fun invoke(body: SequenceExecutionRequest): SequenceExecutionData
}

class UpdateSequenceExecutionUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : UpdateSequenceExecutionUseCase {
        override suspend fun invoke(body: SequenceExecutionRequest): SequenceExecutionData {
            return repository.updateSequenceExecution(body)
        }
    }
