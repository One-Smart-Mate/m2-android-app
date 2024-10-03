package com.ih.osm.domain.usecase.user

import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface UpdateTokenUseCase {
    suspend operator fun invoke(token: String)
}

class UpdateTokenUseCaseImpl
@Inject
constructor(
    private val localRepository: LocalRepository,
    private val authRepository: AuthRepository
) : UpdateTokenUseCase {
    override suspend fun invoke(token: String) {
        localRepository.getUser()?.let {
            authRepository.updateToken(
                UpdateTokenRequest(userId = it.userId.toInt(), appToken = token)
            )
        }
    }
}
