package com.ih.osm.domain.usecase.card

import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

/**
 * UseCase for refreshing cards (pull-to-refresh)
 *
 * This UseCase handles card refresh operations, resetting to the first page
 * and fetching fresh data from the server. Ideal for pull-to-refresh functionality.
 *
 * Business Logic:
 * - Validates level ID
 * - Resets to page 1
 * - Fetches first page of cards (default 20 items)
 * - Returns fresh data without caching intermediates
 * - Provides user feedback on refresh completion
 *
 * Note: This does not clear local card cache - that's handled by the repository
 * if needed based on sync strategy.
 */
interface RefreshCardsUseCase {
    /**
     * Refreshes cards for a level
     *
     * @param levelId The level ID to refresh cards for (must not be blank)
     * @param limit Items per page for refresh (default: 20)
     * @return Result wrapping list of refreshed cards or error
     */
    suspend operator fun invoke(
        levelId: String,
        limit: Int = 20,
    ): Result<List<Card>>
}

class RefreshCardsUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) : RefreshCardsUseCase {
        companion object {
            private const val REFRESH_PAGE = 1
            private const val MIN_LIMIT = 1
            private const val MAX_LIMIT = 100
            private const val DEFAULT_LIMIT = 20
        }

        override suspend fun invoke(
            levelId: String,
            limit: Int,
        ): Result<List<Card>> {
            // Validate inputs
            if (levelId.isBlank()) {
                return Result.Error("Level ID cannot be blank")
            }

            val validatedLimit =
                when {
                    limit < MIN_LIMIT -> MIN_LIMIT
                    limit > MAX_LIMIT -> MAX_LIMIT
                    else -> limit
                }.also {
                    if (it != limit) {
                        // Log adjustment
                        println("Adjusted refresh limit from $limit to $it")
                    }
                }

            return try {
                // Fetch first page of cards (refresh always starts from page 1)
                val cards =
                    cardRepository.getRemoteByLevel(
                        levelId = levelId,
                        page = REFRESH_PAGE,
                        limit = validatedLimit,
                    )

                // Note: For a full refresh implementation, you might want to:
                // 1. Clear cached cards for this level
                // 2. Update local database with fresh data
                // 3. Emit loading state before fetching
                // These are typically handled in the ViewModel/UI layer

                Result.Success(cards)
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to refresh cards for level $levelId: ${e.message ?: "Unknown error"}",
                    throwable = e,
                )
            }
        }
    }
