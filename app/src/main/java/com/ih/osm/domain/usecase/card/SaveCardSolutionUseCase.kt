package com.ih.osm.domain.usecase.card

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
import com.ih.osm.ui.utils.STATUS_P
import com.ih.osm.ui.utils.STATUS_R
import javax.inject.Inject

interface SaveCardSolutionUseCase {
    suspend operator fun invoke(
        solutionType: String,
        cardId: String,
        userSolutionId: String,
        comments: String,
        evidences: List<Evidence> = emptyList(),
        saveLocal: Boolean = true,
        remoteEvidences: List<CreateEvidenceRequest> = emptyList(),
    ): Card?
}

class SaveCardSolutionUseCaseImpl
    @Inject
    constructor(
        private val authRepo: AuthRepository,
        private val cardRepo: CardRepository,
        private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        private val employeeRepo: EmployeeRepository,
        private val evidenceRepo: EvidenceRepository,
        private val solutionRepo: SolutionRepository,
    ) : SaveCardSolutionUseCase {
        override suspend fun invoke(
            solutionType: String,
            cardId: String,
            userSolutionId: String,
            comments: String,
            evidences: List<Evidence>,
            saveLocal: Boolean,
            remoteEvidences: List<CreateEvidenceRequest>,
        ): Card? {
            return try {
                var card: Card? = cardRepo.get(cardId)
                val userAppSolution = authRepo.get()
                val userSolution =
                    employeeRepo.getAll().firstOrNull { it.id == userSolutionId }
                if (saveLocal) {
                    evidences.forEach {
                        evidenceRepo.save(it)
                    }
                }
                val solutionEntity =
                    SolutionEntity(
                        solutionType = solutionType,
                        cardId = cardId,
                        userSolutionId = userSolutionId,
                        comments = comments,
                    )
                return when (solutionType) {
                    DEFINITIVE_SOLUTION -> {
                        if (saveLocal) {
                            solutionRepo.save(solutionEntity)
                            card =
                                card?.copy(
                                    commentsAtCardDefinitiveSolution = comments,
                                    userAppDefinitiveSolutionId = userAppSolution?.userId,
                                    userAppDefinitiveSolutionName = userAppSolution?.name,
                                    userDefinitiveSolutionId = userSolution?.id,
                                    userDefinitiveSolutionName = userSolution?.name,
                                    status = STATUS_R,
                                )
                        } else {
                            val request =
                                CreateDefinitiveSolutionRequest(
                                    cardId = cardId.toInt(),
                                    userDefinitiveSolutionId = userSolutionId.toInt(),
                                    userAppDefinitiveSolutionId =
                                        userAppSolution?.userId?.toInt()
                                            .defaultIfNull(0),
                                    evidences = remoteEvidences,
                                    comments = comments,
                                )
                            LoggerHelperManager.logDefinitiveSolution(request)
                            card = solutionRepo.saveRemoteDefinitive(request)
                        }
                        card?.let {
                            cardRepo.save(it)
                        }
                        card
                    }

                    PROVISIONAL_SOLUTION -> {
                        if (saveLocal) {
                            solutionRepo.save(solutionEntity)
                            card =
                                card?.copy(
                                    commentsAtCardProvisionalSolution = comments,
                                    userAppProvisionalSolutionId = userAppSolution?.userId,
                                    userAppProvisionalSolutionName = userAppSolution?.name,
                                    userProvisionalSolutionId = userSolution?.id,
                                    userProvisionalSolutionName = userSolution?.name,
                                    status = STATUS_P,
                                )
                        } else {
                            val request =
                                CreateProvisionalSolutionRequest(
                                    cardId = cardId.toInt(),
                                    userProvisionalSolutionId = userSolutionId.toInt(),
                                    userAppProvisionalSolutionId =
                                        userAppSolution?.userId?.toInt()
                                            .defaultIfNull(0),
                                    evidences = remoteEvidences,
                                    comments = comments,
                                )
                            LoggerHelperManager.logProvisionalSolution(request)
                            card = solutionRepo.saveRemoteProvisional(request)
                        }
                        card?.let {
                            cardRepo.save(it)
                        }
                        card
                    }

                    else -> {
                        null
                    }
                }
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                firebaseAnalyticsHelper.logSyncCardException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
