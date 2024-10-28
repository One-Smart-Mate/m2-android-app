package com.ih.osm.domain.usecase.catalogs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.cardtype.LocalCardTypeRepository
import com.ih.osm.domain.repository.employee.LocalEmployeeRepository
import com.ih.osm.domain.repository.level.LocalLevelRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.repository.preclassifier.LocalPreclassifierRepository
import com.ih.osm.domain.repository.priority.LocalPriorityRepository
import javax.inject.Inject

interface CleanCatalogsUseCase {
    suspend operator fun invoke(): Boolean
}

class CleanCatalogsUseCaseImpl
@Inject
constructor(
    private val localRepository: LocalRepository,
    private val localCardRepo: LocalCardRepository,
    private val localCardTypeRepo: LocalCardTypeRepository,
    private val localEmployeeRepo: LocalEmployeeRepository,
    private val localPreclassifierRepo: LocalPreclassifierRepository,
    private val localPriorityRepo: LocalPriorityRepository,
    private val localLevelRepo: LocalLevelRepository
) : CleanCatalogsUseCase {
    override suspend fun invoke(): Boolean {
        return try {
            localCardRepo.deleteAll()
            localPreclassifierRepo.deleteAll()
            localPriorityRepo.deleteAll()
            localCardTypeRepo.deleteAll()
            localLevelRepo.deleteAll()
            localRepository.deleteEvidences()
            localEmployeeRepo.deleteAll()
            localRepository.removeSolutions()
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}
