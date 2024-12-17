package com.ih.osm.domain.usecase.user

import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface UpdateTokenUseCase {
    suspend operator fun invoke(token: String)
}

class UpdateTokenUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : UpdateTokenUseCase {
        override suspend fun invoke(token: String) {
            authRepository.get()?.let {
                authRepository.updateToken(
                    UpdateTokenRequest(userId = it.userId.toInt(), appToken = token),
                )
            }
        }
    }
