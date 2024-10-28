package com.ih.osm.domain.usecase.catalogs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface CleanCatalogsUseCase {
    suspend operator fun invoke(): Boolean
}

class CleanCatalogsUseCaseImpl
@Inject
constructor(
    private val localRepository: LocalRepository,
    private val localCardRepository: LocalCardRepository
) : CleanCatalogsUseCase {
    override suspend fun invoke(): Boolean {
        return try {
            localCardRepository.deleteAll()
            localRepository.removePreclassifiers()
            localRepository.removePriorities()
            localRepository.removeCardTypes()
            localRepository.removeLevels()
            localRepository.deleteEvidences()
            localRepository.deleteEmployees()
            localRepository.removeSolutions()
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}
