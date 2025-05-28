package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

class CreateCiltEvidenceUseCase
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) {
        suspend operator fun invoke(request: CiltEvidenceRequest) {
            repository.createEvidence(request)
        }
    }
