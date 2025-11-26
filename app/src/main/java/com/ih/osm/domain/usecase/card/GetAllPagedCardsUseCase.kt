package com.ih.osm.domain.usecase.card

import com.ih.osm.domain.model.PaginatedCards
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

interface GetAllPagedCardsUseCase {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
        syncRemote: Boolean = false,
    ): Result<PaginatedCards>
}

class GetAllPagedCardsUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) : GetAllPagedCardsUseCase {
        companion object {
            private const val MIN_PAGE = 1
            private const val MIN_LIMIT = 1
            private const val MAX_LIMIT = 100
        }

        override suspend fun invoke(
            page: Int,
            limit: Int,
            syncRemote: Boolean,
        ): Result<PaginatedCards> {
            if (page < MIN_PAGE) {
                return Result.Error("Page must be at least $MIN_PAGE")
            }

            val validatedLimit =
                when {
                    limit < MIN_LIMIT -> MIN_LIMIT
                    limit > MAX_LIMIT -> MAX_LIMIT
                    else -> limit
                }

            return try {
                val cards =
                    if (syncRemote) {
                        cardRepository.getAllRemoteByUser(
                            page = page,
                            limit = validatedLimit,
                        )
                    } else {
                        val allLocalCards = cardRepository.getAll()
                        val startIndex = (page - 1) * validatedLimit
                        val endIndex = (startIndex + validatedLimit).coerceAtMost(allLocalCards.size)

                        if (startIndex >= allLocalCards.size) {
                            emptyList()
                        } else {
                            allLocalCards.subList(startIndex, endIndex)
                        }
                    }

                val hasNextPage = cards.size >= validatedLimit
                val totalCards = if (hasNextPage) (page * validatedLimit) + 1 else (page - 1) * validatedLimit + cards.size
                val totalPages = if (hasNextPage) page + 1 else page

                val paginatedCards =
                    PaginatedCards(
                        cards = cards,
                        currentPage = page,
                        pageSize = validatedLimit,
                        totalCards = totalCards,
                        totalPages = totalPages,
                        hasNextPage = hasNextPage,
                    )

                Result.Success(paginatedCards)
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to load cards: ${e.message ?: "Unknown error"}",
                    throwable = e,
                )
            }
        }
    }
