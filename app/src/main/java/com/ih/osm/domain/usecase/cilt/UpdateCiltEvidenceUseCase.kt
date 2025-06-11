package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.UpdateCiltEvidenceRequest
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface UpdateCiltEvidenceUseCase {
    suspend operator fun invoke(body: UpdateCiltEvidenceRequest): CiltSequenceEvidence
}

class UpdateCiltEvidenceUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : UpdateCiltEvidenceUseCase {
        override suspend fun invoke(body: UpdateCiltEvidenceRequest): CiltSequenceEvidence {
            return repository.updateEvidence(body)
        }
    }
