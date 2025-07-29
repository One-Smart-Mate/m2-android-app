package com.ih.osm.domain.usecase.card

import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import javax.inject.Inject

interface DeleteCardUseCase {
    suspend operator fun invoke(cardUUID: String): Boolean
}

class DeleteCardUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
        private val solutionsRepository: SolutionRepository,
        private val evidenceRepository: EvidenceRepository,
        private val sharedPreferences: SharedPreferences,
    ) : DeleteCardUseCase {
        override suspend fun invoke(cardUUID: String): Boolean {
            evidenceRepository.deleteByCard(cardUUID)
            solutionsRepository.deleteAllByCard(cardUUID)
            cardRepository.delete(cardUUID)
            sharedPreferences.removeCiltCard()
            return true
        }
    }
