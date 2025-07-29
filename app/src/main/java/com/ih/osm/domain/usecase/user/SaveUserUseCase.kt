package com.ih.osm.domain.usecase.user

import com.ih.osm.domain.model.User
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface SaveUserUseCase {
    suspend operator fun invoke(user: User): Long
}

class SaveUserUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : SaveUserUseCase {
        override suspend fun invoke(user: User): Long {
            return authRepository.save(user)
        }
    }
