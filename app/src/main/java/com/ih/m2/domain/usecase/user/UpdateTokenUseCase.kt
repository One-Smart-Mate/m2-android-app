package com.ih.m2.domain.usecase.user

import com.ih.m2.data.model.UpdateTokenRequest
import com.ih.m2.domain.repository.auth.AuthRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.extensions.defaultIfNull
import javax.inject.Inject

interface UpdateTokenUseCase {
    suspend operator fun invoke(token: String)
}

class UpdateTokenUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val authRepository: AuthRepository
): UpdateTokenUseCase {

    override suspend fun invoke(token: String) {
        localRepository.getUser()?.let {
            authRepository.updateToken(UpdateTokenRequest(userId = it.userId.toInt(), appToken = token))
        }
    }
}