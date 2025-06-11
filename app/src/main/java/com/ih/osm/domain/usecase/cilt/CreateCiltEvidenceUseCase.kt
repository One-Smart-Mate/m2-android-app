package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface CreateCiltEvidenceUseCase {
    suspend operator fun invoke(body: CiltEvidenceRequest): CiltSequenceEvidence
}

class CreateCiltEvidenceUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : CreateCiltEvidenceUseCase {
        override suspend fun invoke(body: CiltEvidenceRequest): CiltSequenceEvidence {
            return repository.createEvidence(body)
        }
    }
