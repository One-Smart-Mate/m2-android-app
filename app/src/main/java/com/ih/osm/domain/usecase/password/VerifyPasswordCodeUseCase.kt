package com.ih.osm.domain.usecase.password

import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface VerifyPasswordCodeUseCase {
    suspend operator fun invoke(request: RestorePasswordRequest)
}

class VerifyPasswordCodeUseCaseImpl
@Inject
constructor(
    private val authRepository: AuthRepository
) : VerifyPasswordCodeUseCase {
    override suspend fun invoke(request: RestorePasswordRequest) {
        authRepository.verifyPasswordCode(request)
    }
}
