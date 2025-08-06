package com.ih.osm.domain.usecase.session

import com.ih.osm.domain.model.Session
import com.ih.osm.domain.repository.session.SessionRepository
import javax.inject.Inject

interface SaveSessionUseCase {
    suspend operator fun invoke(session: Session)
}

class SaveSessionUseCaseImpl
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
    ) : SaveSessionUseCase {
        override suspend fun invoke(session: Session) {
            sessionRepository.save(session)
        }
    }
