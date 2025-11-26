package com.ih.osm.domain.usecase.card

import android.util.Log
import com.ih.osm.domain.model.PaginatedCards
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

/**
 * UseCase for fetching all paginated cards for the current user
 *
 * This UseCase retrieves ALL cards (across all levels) with pagination support
 * for infinite scroll functionality.
 *
 * Business Logic:
 * - Validates pagination parameters (page >= 1, limit 1-100)
 * - Fetches cards from repository with pagination
 * - Transforms response to PaginatedCards domain model
 * - Calculates pagination metadata (hasNextPage, totalPages)
 * - Returns empty result if no cards found
 *
 * Default page size: 20 cards (recommended for mobile infinite scroll)
 */
interface GetAllPagedCardsUseCase {
    /**
     * Loads paginated cards for the current user
     *
     * @param page Page number (default: 1, must be >= 1)
     * @param limit Items per page (default: 20, range: 1-100)
     * @param syncRemote Whether to sync from remote server (default: false)
     * @return Result wrapping PaginatedCards or error
     */
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
            private const val TAG = "GetAllPagedCardsUseCase"
            private const val MIN_PAGE = 1
            private const val MIN_LIMIT = 1
            private const val MAX_LIMIT = 100
            private const val DEFAULT_LIMIT = 20
        }

        override suspend fun invoke(
            page: Int,
            limit: Int,
            syncRemote: Boolean,
        ): Result<PaginatedCards> {
            Log.d(TAG, "===== EXECUTE: page=$page, limit=$limit, syncRemote=$syncRemote =====")

            // Validate inputs
            if (page < MIN_PAGE) {
                Log.e(TAG, "ERROR: Invalid page number: $page")
                return Result.Error("Page must be at least $MIN_PAGE")
            }

            val validatedLimit =
                when {
                    limit < MIN_LIMIT -> MIN_LIMIT
                    limit > MAX_LIMIT -> MAX_LIMIT
                    else -> limit
                }

            Log.d(TAG, "Validated limit: $validatedLimit")

            return try {
                val cards =
                    if (syncRemote) {
                        Log.d(TAG, "Fetching from REMOTE...")
                        cardRepository.getAllRemoteByUser(
                            page = page,
                            limit = validatedLimit,
                        )
                    } else {
                        Log.d(TAG, "Fetching from LOCAL database...")
                        // For local, we need to manually paginate
                        val allLocalCards = cardRepository.getAll()
                        val startIndex = (page - 1) * validatedLimit
                        val endIndex = (startIndex + validatedLimit).coerceAtMost(allLocalCards.size)

                        if (startIndex >= allLocalCards.size) {
                            emptyList()
                        } else {
                            allLocalCards.subList(startIndex, endIndex)
                        }
                    }

                Log.d(TAG, "Fetched ${cards.size} cards")

                // Determine if there's a next page
                // If we got a full page, there might be more
                val hasNextPage = cards.size >= validatedLimit

                val totalCards = if (hasNextPage) (page * validatedLimit) + 1 else (page - 1) * validatedLimit + cards.size
                val totalPages = if (hasNextPage) page + 1 else page

                Log.d(TAG, "hasNextPage=$hasNextPage, totalCards~$totalCards, totalPages~$totalPages")

                val paginatedCards =
                    PaginatedCards(
                        cards = cards,
                        currentPage = page,
                        pageSize = validatedLimit,
                        totalCards = totalCards, // Approximate
                        totalPages = totalPages, // Approximate
                        hasNextPage = hasNextPage,
                    )

                Log.d(TAG, "SUCCESS: Returning ${cards.size} cards for page $page")
                Result.Success(paginatedCards)
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: ${e.message}", e)
                Result.Error(
                    message = "Failed to load cards: ${e.message ?: "Unknown error"}",
                    throwable = e,
                )
            }
        }
    }
