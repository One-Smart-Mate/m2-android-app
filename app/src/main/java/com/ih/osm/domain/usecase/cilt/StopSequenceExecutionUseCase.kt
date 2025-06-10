package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.SequenceExecution
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface StopSequenceExecutionUseCase {
    suspend operator fun invoke(body: StopSequenceExecutionRequest): SequenceExecution
}

class StopSequenceExecutionUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : StopSequenceExecutionUseCase {
        override suspend fun invoke(body: StopSequenceExecutionRequest): SequenceExecution {
            return repository.stopSequenceExecution(body)
        }
    }
