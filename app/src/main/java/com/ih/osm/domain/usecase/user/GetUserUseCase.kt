package com.ih.osm.domain.usecase.user

import com.ih.osm.domain.model.User
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface GetUserUseCase {
    suspend operator fun invoke(): User?
}

class GetUserUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : GetUserUseCase {
        override suspend fun invoke(): User? = authRepository.get()
    }
