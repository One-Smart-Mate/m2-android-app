package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.SequenceExecution
import com.ih.osm.domain.repository.cilt.CiltRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.ui.extensions.getCurrentDateTimeUtc
import javax.inject.Inject

interface StopSequenceExecutionUseCase {
    suspend operator fun invoke(
        body: StopSequenceExecutionRequest,
        evidences: List<Evidence>,
    ): SequenceExecution
}

class StopSequenceExecutionUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
        private val firebaseRepository: FirebaseStorageRepository,
    ) : StopSequenceExecutionUseCase {
        override suspend fun invoke(
            body: StopSequenceExecutionRequest,
            evidences: List<Evidence>,
        ): SequenceExecution {
            val response = repository.stopSequenceExecution(body)
            for (evidence in evidences) {
                val uploadedUrl = firebaseRepository.uploadEvidence(evidence)
                if (uploadedUrl.isNotEmpty()) {
                    val request =
                        CiltEvidenceRequest(
                            executionId = body.id,
                            evidenceUrl = uploadedUrl,
                            type = evidence.type,
                            createdAt = getCurrentDateTimeUtc(),
                        )
                    repository.createEvidence(request)
                }
            }
            return response
        }
    }
