package com.ih.osm.domain.usecase.catalogs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

interface CleanCatalogsUseCase {
    suspend operator fun invoke(): Boolean
}

class CleanCatalogsUseCaseImpl
    @Inject
    constructor(
        private val cardRepo: CardRepository,
        private val cardTypeRepo: CardTypeRepository,
        private val employeeRepo: EmployeeRepository,
        private val preclassifierRepo: PreclassifierRepository,
        private val priorityRepo: PriorityRepository,
        private val levelRepo: LevelRepository,
        private val evidenceRepo: EvidenceRepository,
        private val solutionRepo: SolutionRepository,
    ) : CleanCatalogsUseCase {
        override suspend fun invoke(): Boolean =
            try {
                cardRepo.deleteAll()
                preclassifierRepo.deleteAll()
                priorityRepo.deleteAll()
                cardTypeRepo.deleteAll()
                levelRepo.deleteAll()
                evidenceRepo.deleteAll()
                employeeRepo.deleteAll()
                solutionRepo.deleteAll()
                true
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
                false
            }
    }
