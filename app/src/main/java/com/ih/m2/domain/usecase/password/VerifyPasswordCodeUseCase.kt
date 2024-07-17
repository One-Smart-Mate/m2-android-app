package com.ih.m2.domain.usecase.password

import com.ih.m2.data.model.RestorePasswordRequest
import com.ih.m2.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface VerifyPasswordCodeUseCase {
    suspend operator fun invoke(request: RestorePasswordRequest)

}

class VerifyPasswordCodeUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : VerifyPasswordCodeUseCase {
    override suspend fun invoke(request: RestorePasswordRequest) {
        authRepository.verifyPasswordCode(request)
    }
}