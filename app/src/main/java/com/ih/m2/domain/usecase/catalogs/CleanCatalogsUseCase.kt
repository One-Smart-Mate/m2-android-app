package com.ih.m2.domain.usecase.catalogs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface CleanCatalogsUseCase {
    suspend operator fun invoke(): Boolean
}

class CleanCatalogsUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
) : CleanCatalogsUseCase {

    override suspend fun invoke(): Boolean {
        return try {
            localRepository.removeCards()
            localRepository.removePreclassifiers()
            localRepository.removePriorities()
            localRepository.removeCardTypes()
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}