package com.ih.osm.domain.usecase.logout

import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import javax.inject.Inject

interface LogoutUseCase {
    suspend operator fun invoke(): Int
}

class LogoutUseCaseImpl
@Inject
constructor(
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
