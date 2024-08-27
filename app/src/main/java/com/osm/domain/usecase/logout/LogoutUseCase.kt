package com.osm.domain.usecase.logout

import com.osm.core.preferences.SharedPreferences
import com.osm.domain.repository.local.LocalRepository
import com.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import javax.inject.Inject

interface LogoutUseCase {
    suspend operator fun invoke(): Int
}

class LogoutUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val cleanCatalogsUseCase: CleanCatalogsUseCase,
    private val sharedPreferences: SharedPreferences
) : LogoutUseCase {
    override suspend fun invoke(): Int {
        cleanCatalogsUseCase()
        sharedPreferences.clearPreferences()
        return localRepository.logout()
    }
}