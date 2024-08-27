package com.osm.domain.usecase.user

import com.osm.data.model.UpdateTokenRequest
import com.osm.domain.repository.auth.AuthRepository
import com.osm.domain.repository.local.LocalRepository
import com.osm.ui.extensions.defaultIfNull
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