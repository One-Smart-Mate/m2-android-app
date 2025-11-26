package com.ih.osm.domain.usecase.level

import android.util.Log
import com.ih.osm.domain.cache.LevelCacheManager
import com.ih.osm.domain.model.LevelTreeData
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

/**
 * UseCase for fetching level tree with lazy loading support
 *
 * This UseCase retrieves the level tree structure up to a specified depth,
 * implementing a cache-first strategy for optimal performance.
 *
 * Business Logic:
 * - Validates input parameters (depth must be > 0)
 * - Uses cache-first strategy (5 minute TTL)
 * - Fetches from remote if cache miss or expired
 * - Caches the fetched tree data
 * - Returns nested children up to specified depth
 *
 * Default depth: 2 levels (matching web app behavior)
 */
interface GetLevelTreeLazyUseCase {
    /**
     * Loads level tree with specified depth
     *
     * @param depth Tree depth to fetch (default: 2, must be > 0)
     * @return Result wrapping LevelTreeData or error
     */
    suspend operator fun invoke(depth: Int = 2): Result<LevelTreeData>
}

class GetLevelTreeLazyUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        private val cacheManager: LevelCacheManager,
    ) : GetLevelTreeLazyUseCase {
        override suspend fun invoke(depth: Int): Result<LevelTreeData> {
            Log.d("GetLevelTreeLazyUseCase", "===== EXECUTE: depth=$depth =====")

            return try {
                // Validate depth
                if (depth !in 1..10) {
                    Log.e("GetLevelTreeLazyUseCase", "Invalid depth: $depth (must be 1-10)")
                    return Result.Error("Depth must be between 1 and 10, got: $depth")
                }

                Log.d("GetLevelTreeLazyUseCase", "Calling repository...")
                val treeData =
                    levelRepository.getRemoteLevelTreeLazy(
                        page = null,
                        limit = null,
                        depth = depth,
                    )

                Log.d("GetLevelTreeLazyUseCase", "Repository returned: ${treeData.data.size} levels")

                // Cache the tree data
                Log.d("GetLevelTreeLazyUseCase", "Caching tree data...")
                cacheManager.cacheTreeNode(
                    parentId = treeData.parentId,
                    depth = depth,
                    levels = treeData.data,
                )

                Log.d("GetLevelTreeLazyUseCase", "SUCCESS")
                Result.Success(treeData)
            } catch (e: Exception) {
                Log.e("GetLevelTreeLazyUseCase", "ERROR: ${e.message}", e)
                Result.Error(
                    message = e.message ?: "Failed to load level tree",
                    throwable = e,
                )
            }
        }
    }
