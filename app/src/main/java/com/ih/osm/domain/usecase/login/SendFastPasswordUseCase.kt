package com.ih.osm.domain.usecase.login

import com.ih.osm.data.model.SendFastPasswordRequest
import com.ih.osm.data.model.SendFastPasswordResponse
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface SendFastPasswordUseCase {
    suspend operator fun invoke(body: SendFastPasswordRequest): SendFastPasswordResponse
}

class SendFastLoginUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : SendFastPasswordUseCase {
        override suspend fun invoke(body: SendFastPasswordRequest): SendFastPasswordResponse {
            return authRepository.sendFastPassword(body)
        }
    }
