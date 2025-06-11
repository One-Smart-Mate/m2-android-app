package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.domain.model.SequenceExecution
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface StartSequenceExecutionUseCase {
    suspend operator fun invoke(body: StartSequenceExecutionRequest): SequenceExecution
}

class StartSequenceExecutionUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : StartSequenceExecutionUseCase {
        override suspend fun invoke(body: StartSequenceExecutionRequest): SequenceExecution {
            return repository.startSequenceExecution(body)
        }
    }
