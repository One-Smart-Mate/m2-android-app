package com.ih.osm.domain.usecase.user

import com.ih.osm.BuildConfig
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.ui.utils.ANDROID_SO
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
                    UpdateTokenRequest(
                        userId = it.userId.toInt(),
                        appToken = token,
                        osName = ANDROID_SO.uppercase(),
                        osVersion = BuildConfig.VERSION_NAME,
                    ),
                )
            }
        }
    }
