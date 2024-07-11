package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.core.FileHelper
import com.ih.m2.data.model.CreateDefinitiveSolutionRequest
import com.ih.m2.data.model.CreateEvidenceRequest
import com.ih.m2.data.model.CreateProvisionalSolutionRequest
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.utils.DEFINITIVE_SOLUTION
import com.ih.m2.ui.utils.PROVISIONAL_SOLUTION
import javax.inject.Inject

interface SaveCardSolutionUseCase {
    suspend operator fun invoke(
        solutionType: String,
        cardId: Int,
        userSolutionId: String,
        comments: String,
        evidences: List<Evidence>
    ): Card?
}

class SaveCardSolutionUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val localRepository: LocalRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val fileHelper: FileHelper
) : SaveCardSolutionUseCase {

    override suspend fun invoke(
        solutionType: String,
        cardId: Int,
        userSolutionId: String,
        comments: String,
        evidences: List<Evidence>
    ): Card? {
        val evidenceList = mutableListOf<CreateEvidenceRequest>()
        val userAppSolutionId = localRepository.getUser()?.userId.orEmpty()
        evidences.forEach {
            val url = firebaseStorageRepository.uploadEvidence(it)
            Log.e("test","Provisional solution $url")
            if (url.isNotEmpty()) {
                evidenceList.add(CreateEvidenceRequest(it.type, url))
            }
        }
        return when (solutionType) {
            DEFINITIVE_SOLUTION -> {
                val request = CreateDefinitiveSolutionRequest(
                    cardId = cardId,
                    userDefinitiveSolutionId = userSolutionId.toInt(),
                    userAppDefinitiveSolutionId = userAppSolutionId.toInt(),
                    evidences = evidenceList.toList(),
                    comments = comments
                )
                fileHelper.logDefinitiveSolution(request)
                val card = cardRepository.saveDefinitiveSolution(request)
                Log.e("Test","Solution New card $card")
                localRepository.saveCard(card)
                card
            }
            PROVISIONAL_SOLUTION -> {
                val request = CreateProvisionalSolutionRequest(
                    cardId = cardId,
                    userProvisionalSolutionId = userSolutionId.toInt(),
                    userAppProvisionalSolutionId = userAppSolutionId.toInt(),
                    evidences = evidenceList.toList(),
                    comments = comments
                )
                fileHelper.logProvisionalSolution(request)
                val card = cardRepository.saveProvisionalSolution(request)
                Log.e("Test","Solution New card $card")
                localRepository.saveCard(card)
                card
            }
            else -> {
                null
            }
        }
    }

}