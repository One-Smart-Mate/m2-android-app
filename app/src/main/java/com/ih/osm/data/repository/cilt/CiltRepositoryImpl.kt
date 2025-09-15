package com.ih.osm.data.repository.cilt

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.SequenceExecution
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
        override suspend fun getCilts(date: String): CiltData {
            val userId = authRepo.get()?.userId.orEmpty()
            return networkRepository.getCilts(userId, date)
        }

        override suspend fun startSequenceExecution(body: StartSequenceExecutionRequest): SequenceExecution =
            networkRepository.startSequenceExecution(body)

        override suspend fun stopSequenceExecution(body: StopSequenceExecutionRequest): SequenceExecution =
            networkRepository.stopSequenceExecution(body)

        override suspend fun createEvidence(body: CiltEvidenceRequest): CiltSequenceEvidence = networkRepository.createEvidence(body)

        override suspend fun getOplById(id: String): Opl = networkRepository.getOplById(id)

        override suspend fun getSequence(id: Int): Sequence = networkRepository.getSequence(id)
    }
