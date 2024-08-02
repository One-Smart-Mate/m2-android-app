package com.ih.m2.domain.usecase.logout

import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.usecase.catalogs.CleanCatalogsUseCase
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