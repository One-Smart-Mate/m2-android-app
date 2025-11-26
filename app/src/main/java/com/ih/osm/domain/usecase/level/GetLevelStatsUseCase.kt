package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.cache.LevelCacheManager
import com.ih.osm.domain.model.LevelStats
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

/**
 * UseCase for fetching level statistics
 *
 * This UseCase retrieves statistical information about the level hierarchy,
 * such as total count, max depth, and performance warnings.
 *
 * Business Logic:
 * - Checks cache first (5 minute TTL)
 * - Returns cached stats if available
 * - Fetches from remote on cache miss
 * - Caches fetched statistics
 * - Returns stats with performance warnings if applicable
 *
 * Statistics are lightweight and cached in SharedPreferences.
 */
interface GetLevelStatsUseCase {
    /**
     * Loads level statistics
     *
     * @return Result wrapping LevelStats or error
     */
    suspend operator fun invoke(): Result<LevelStats>
}

class GetLevelStatsUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        private val cacheManager: LevelCacheManager,
    ) : GetLevelStatsUseCase {
        override suspend fun invoke(): Result<LevelStats> {
            return try {
                // Check cache first
                val cachedStats = cacheManager.getCachedStats()
                if (cachedStats != null) {
                    return Result.Success(cachedStats)
                }

                // Cache miss - fetch from repository
                val stats =
                    levelRepository.getRemoteLevelStats(
                        page = null,
                        limit = null,
                    )

                // Cache the fetched stats
                cacheManager.cacheStats(stats)

                // Return success
                Result.Success(stats)
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to load level statistics: ${e.message ?: "Unknown error"}",
                    throwable = e,
                )
            }
        }
    }
