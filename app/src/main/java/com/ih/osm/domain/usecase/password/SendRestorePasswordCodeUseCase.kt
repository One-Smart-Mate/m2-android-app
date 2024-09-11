package com.ih.osm.domain.usecase.password

import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.domain.repository.auth.AuthRepository
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