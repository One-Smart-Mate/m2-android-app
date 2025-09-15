package com.ih.osm.domain.usecase.cilt

import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface GetSequenceUseCase {
    suspend operator fun invoke(sequenceId: Int): Sequence
}

class GetSequenceUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : GetSequenceUseCase {
        override suspend fun invoke(sequenceId: Int): Sequence = repository.getSequence(sequenceId)
    }
