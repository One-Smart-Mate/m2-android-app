package com.ih.m2.domain.usecase.logout

import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.usecase.catalogs.CleanCatalogsUseCase
import javax.inject.Inject

interface LogoutUseCase {
    suspend operator fun invoke(): Int
}

class LogoutUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val cleanCatalogsUseCase: CleanCatalogsUseCase
) : LogoutUseCase {
    override suspend fun invoke(): Int {
        cleanCatalogsUseCase()
        return localRepository.logout()
    }
}