package com.ih.osm.domain.usecase.card

import android.util.Log
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
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