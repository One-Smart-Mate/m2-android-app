package com.ih.osm.domain.usecase.card

import com.ih.osm.domain.model.PaginatedCards
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

/**
 * UseCase for fetching paginated cards by level
 *
 * This UseCase retrieves cards with pagination support for infinite scroll
 * and paged loading functionality.
 *
 * Business Logic:
 * - Validates pagination parameters (page >= 1, limit 1-100)
 * - Fetches cards for specific level from repository
 * - Transforms response to PaginatedCards domain model
 * - Calculates pagination metadata (hasNextPage, totalPages)
 * - Returns empty result if no cards found
 *
 * Default page size: 20 cards (recommended for mobile infinite scroll)
 */
interface GetPagedCardsUseCase {
    /**
     * Loads paginated cards for a level
     *
     * @param levelId The level ID to fetch cards for (must not be blank)
     * @param page Page number (default: 1, must be >= 1)
     * @param limit Items per page (default: 20, range: 1-100)
     * @return Result wrapping PaginatedCards or error
     */
    suspend operator fun invoke(
        levelId: String,
        page: Int = 1,
        limit: Int = 20,
    ): Result<PaginatedCards>
}

class GetPagedCardsUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) : GetPagedCardsUseCase {
        companion object {
            private const val MIN_PAGE = 1
            private const val MIN_LIMIT = 1
            private const val MAX_LIMIT = 100
            private const val DEFAULT_LIMIT = 20
        }

        override suspend fun invoke(
            levelId: String,
            page: Int,
            limit: Int,
        ): Result<PaginatedCards> {
            // Validate inputs
            if (levelId.isBlank()) {
                return Result.Error("Level ID cannot be blank")
            }

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
                    cardRepository.getRemoteByLevel(
                        levelId = levelId,
                        page = page,
                        limit = validatedLimit,
                    )

                val hasNextPage = cards.size >= validatedLimit
                val totalCards = if (hasNextPage) (page * validatedLimit) + 1 else (page - 1) * validatedLimit + cards.size
                val totalPages = if (hasNextPage) page + 1 else page

                val paginatedCards =
                    PaginatedCards(
                        cards = cards,
                        currentPage = page,
                        pageSize = validatedLimit,
                        totalCards = totalCards, // Approximate
                        totalPages = totalPages, // Approximate
                        hasNextPage = hasNextPage,
                    )

                Result.Success(paginatedCards)
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to load cards for level $levelId: ${e.message ?: "Unknown error"}",
                    throwable = e,
                )
            }
        }
    }
