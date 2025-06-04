package com.ih.osm.data.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.SequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.SequenceExecutionData
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cilt.CiltRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

data class CiltRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
        private val authRepo: AuthRepository,
    ) : CiltRepository {
        override suspend fun getCilts(body: GetCiltsRequest): CiltData {
            return networkRepository.getCilts(body)
        }

        override suspend fun updateSequenceExecution(body: SequenceExecutionRequest): SequenceExecutionData {
            return networkRepository.updateSequenceExecution(body)
        }

        override suspend fun createEvidence(body: CiltEvidenceRequest) {
            networkRepository.createEvidence(body)
        }
    }
