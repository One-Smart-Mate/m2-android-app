package com.ih.osm.domain.usecase.session

import com.ih.osm.domain.model.Session
import com.ih.osm.domain.repository.session.SessionRepository
import javax.inject.Inject

interface GetSessionUseCase {
    suspend operator fun invoke(): Session
}

class GetSessionUseCaseImpl
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
    ) : GetSessionUseCase {
        override suspend fun invoke(): Session {
            return sessionRepository.get()
                ?: throw IllegalStateException("No session found")
        }
    }
