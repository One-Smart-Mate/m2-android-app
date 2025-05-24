package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.SequenceExecutionRequest
import com.ih.osm.domain.model.SequenceExecutionData
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface SequenceExecutionUseCase {
    suspend operator fun invoke(body: SequenceExecutionRequest): SequenceExecutionData
}

class SequenceExecutionUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : SequenceExecutionUseCase {
        override suspend fun invoke(body: SequenceExecutionRequest): SequenceExecutionData {
            return repository.updateSequenceExecution(body)
        }
    }
