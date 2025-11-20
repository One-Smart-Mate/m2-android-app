package com.ih.osm.domain.usecase.logout

import com.google.firebase.messaging.FirebaseMessaging
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import com.ih.osm.domain.usecase.site.ClearSitesUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface LogoutUseCase {
    suspend operator fun invoke(): Int
}

class LogoutUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val cleanCatalogsUseCase: CleanCatalogsUseCase,
        private val sharedPreferences: SharedPreferences,
        private val clearSitesUseCase: ClearSitesUseCase,
    ) : LogoutUseCase {
        override suspend fun invoke(): Int {
            cleanCatalogsUseCase()
            clearSitesUseCase()
            sharedPreferences.clearPreferences()
            FirebaseMessaging.getInstance().deleteToken().await()
            return authRepository.logout()
        }
    }
