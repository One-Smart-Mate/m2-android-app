package com.osm.domain.usecase.card

import android.util.Log
import com.osm.core.file.FileHelper
import com.osm.data.model.CreateDefinitiveSolutionRequest
import com.osm.data.model.CreateEvidenceRequest
import com.osm.data.model.CreateProvisionalSolutionRequest
import com.osm.domain.model.Card
import com.osm.domain.model.Evidence
import com.osm.domain.repository.cards.CardRepository
import com.osm.domain.repository.firebase.FirebaseStorageRepository
import com.osm.domain.repository.local.LocalRepository
import com.osm.ui.utils.DEFINITIVE_SOLUTION
import com.osm.ui.utils.PROVISIONAL_SOLUTION
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