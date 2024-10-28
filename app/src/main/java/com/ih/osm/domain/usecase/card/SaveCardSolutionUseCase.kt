package com.ih.osm.domain.usecase.card

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.employee.LocalEmployeeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
import javax.inject.Inject

interface SaveCardSolutionUseCase {
    suspend operator fun invoke(
        solutionType: String,
        cardId: String,
        userSolutionId: String,
        comments: String,
        evidences: List<Evidence> = emptyList(),
        saveLocal: Boolean = true,
        remoteEvidences: List<CreateEvidenceRequest> = emptyList()
    ): Card?
}

class SaveCardSolutionUseCaseImpl
@Inject
constructor(
    private val remoteRepo: CardRepository,
    private val localRepository: LocalRepository,
    private val localRepo: LocalCardRepository,
    private val fileHelper: FileHelper,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val localEmployeeRepo: LocalEmployeeRepository,
    private val localEvidenceRepo: EvidenceRepository
) : SaveCardSolutionUseCase {
    override suspend fun invoke(
        solutionType: String,
        cardId: String,
        userSolutionId: String,
        comments: String,
        evidences: List<Evidence>,
        saveLocal: Boolean,
        remoteEvidences: List<CreateEvidenceRequest>
    ): Card? {
        return try {
            var card: Card? = localRepo.get(cardId)
            val userAppSolution = localRepository.getUser()
            val userSolution =
                localEmployeeRepo.getAll().firstOrNull { it.id == userSolutionId }
            if (saveLocal) {
                evidences.forEach {
                    localEvidenceRepo.save(it)
                }
            }
            val solutionEntity = SolutionEntity(
                solutionType = solutionType,
                cardId = cardId,
                userSolutionId = userSolutionId,
                comments = comments
            )
            return when (solutionType) {
                DEFINITIVE_SOLUTION -> {
                    if (saveLocal) {
                        localRepository.saveSolution(solutionEntity)
                        card = card?.copy(
                            commentsAtCardDefinitiveSolution = comments,
                            userAppDefinitiveSolutionId = userAppSolution?.userId,
                            userAppDefinitiveSolutionName = userAppSolution?.name,
                            userDefinitiveSolutionId = userSolution?.id,
                            userDefinitiveSolutionName = userSolution?.name
                        )
                    } else {
                        val request =
                            CreateDefinitiveSolutionRequest(
                                cardId = cardId.toInt(),
                                userDefinitiveSolutionId = userSolutionId.toInt(),
                                userAppDefinitiveSolutionId = userAppSolution?.userId?.toInt()
                                    .defaultIfNull(0),
                                evidences = remoteEvidences,
                                comments = comments
                            )
                        fileHelper.logDefinitiveSolution(request)
                        card = remoteRepo.saveDefinitiveSolution(request)
                    }
                    Log.e("Test", "Solution definitive card -> $card")
                    card?.let {
                        localRepo.save(it)
                    }
                    card
                }

                PROVISIONAL_SOLUTION -> {
                    if (saveLocal) {
                        localRepository.saveSolution(solutionEntity)
                        card = card?.copy(
                            commentsAtCardProvisionalSolution = comments,
                            userAppProvisionalSolutionId = userAppSolution?.userId,
                            userAppProvisionalSolutionName = userAppSolution?.name,
                            userProvisionalSolutionId = userSolution?.id,
                            userProvisionalSolutionName = userSolution?.name
                        )
                    } else {
                        val request =
                            CreateProvisionalSolutionRequest(
                                cardId = cardId.toInt(),
                                userProvisionalSolutionId = userSolutionId.toInt(),
                                userAppProvisionalSolutionId = userAppSolution?.userId?.toInt()
                                    .defaultIfNull(0),
                                evidences = remoteEvidences,
                                comments = comments
                            )
                        fileHelper.logProvisionalSolution(request)
                        card = remoteRepo.saveProvisionalSolution(request)
                    }
                    Log.e("Test", "Solution provisional card $card")
                    card?.let {
                        localRepo.save(it)
                    }
                    card
                }

                else -> {
                    null
                }
            }
        } catch (e: Exception) {
            firebaseAnalyticsHelper.logSyncCardException(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            fileHelper.logException(e)
            null
        }
    }
}
