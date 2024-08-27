package com.osm.domain.usecase.password

import com.osm.data.model.RestorePasswordRequest
import com.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface SendRestorePasswordCodeUseCase {
    suspend operator fun invoke(request: RestorePasswordRequest)
}

class SendRestorePasswordCodeUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SendRestorePasswordCodeUseCase {
    override suspend fun invoke(request: RestorePasswordRequest) {
        authRepository.sendRestorePasswordCode(request)
    }
}