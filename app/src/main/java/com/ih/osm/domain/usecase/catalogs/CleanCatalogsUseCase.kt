package com.ih.osm.domain.usecase.catalogs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.preclassifier.LocalPreclassifierRepository
import com.ih.osm.domain.repository.priority.LocalPriorityRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

interface CleanCatalogsUseCase {
    suspend operator fun invoke(): Boolean
}

class CleanCatalogsUseCaseImpl
@Inject
constructor(
    private val localCardRepo: LocalCardRepository,
    private val cardTypeRepo: CardTypeRepository,
    private val employeeRepo: EmployeeRepository,
    private val localPreclassifierRepo: LocalPreclassifierRepository,
    private val localPriorityRepo: LocalPriorityRepository,
    private val levelRepo: LevelRepository,
    private val evidenceRepo: EvidenceRepository,
    private val solutionRepo: SolutionRepository
) : CleanCatalogsUseCase {
    override suspend fun invoke(): Boolean {
        return try {
            localCardRepo.deleteAll()
            localPreclassifierRepo.deleteAll()
            localPriorityRepo.deleteAll()
            cardTypeRepo.deleteAll()
            levelRepo.deleteAll()
            evidenceRepo.deleteAll()
            employeeRepo.deleteAll()
            solutionRepo.deleteAll()
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}
