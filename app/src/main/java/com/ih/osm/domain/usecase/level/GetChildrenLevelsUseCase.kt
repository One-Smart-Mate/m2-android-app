package com.ih.osm.domain.usecase.level

import android.util.Log
import com.ih.osm.domain.cache.LevelCacheManager
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

/**
 * UseCase for loading children of a specific level lazily
 *
 * This UseCase implements the lazy loading pattern for tree hierarchies,
 * fetching children only when a node is expanded. It follows the pattern
 * from the web app's useLazyNode hook.
 *
 * Business Logic:
 * - Validates parent ID
 * - Checks cache first (5 minute TTL)
 * - Returns cached children if available
 * - Fetches from remote on cache miss
 * - Caches fetched children
 * - Returns empty list if no children found
 *
 * This is the critical UseCase for on-demand tree node loading.
 */
interface GetChildrenLevelsUseCase {
    /**
     * Loads children for a parent level
     *
     * @param parentId The parent level ID (must not be blank)
     * @return Result wrapping list of child levels (empty list if none)
     */
    suspend operator fun invoke(parentId: String): Result<List<Level>>
}

class GetChildrenLevelsUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        private val cacheManager: LevelCacheManager,
    ) : GetChildrenLevelsUseCase {
        override suspend fun invoke(parentId: String): Result<List<Level>> {
            Log.d("GetChildrenLevelsUseCase", "===== EXECUTE: parentId=$parentId =====")

            return try {
                // Check cache first
                Log.d("GetChildrenLevelsUseCase", "Checking cache...")
                val cachedChildren = cacheManager.getCachedChildren(parentId)

                if (cachedChildren != null && cachedChildren.isNotEmpty()) {
                    Log.d("GetChildrenLevelsUseCase", "CACHE HIT: ${cachedChildren.size} children")
                    return Result.Success(cachedChildren)
                }

                Log.d("GetChildrenLevelsUseCase", "CACHE MISS: Fetching from repository...")

                // Parse parentId to String (repository expects String)
                if (parentId.isBlank()) {
                    Log.e("GetChildrenLevelsUseCase", "Invalid parentId: $parentId")
                    return Result.Error("Invalid parent ID: $parentId")
                }

                // Fetch from repository
                Log.d("GetChildrenLevelsUseCase", "Calling repository...")
                val children =
                    levelRepository.getRemoteChildrenLevels(
                        parentId = parentId,
                        page = null,
                        limit = null,
                    )

                Log.d("GetChildrenLevelsUseCase", "Repository returned: ${children.size} children")

                // Cache the children
                if (children.isNotEmpty()) {
                    Log.d("GetChildrenLevelsUseCase", "Caching ${children.size} children...")
                    cacheManager.cacheChildren(parentId, children)
                }

                Log.d("GetChildrenLevelsUseCase", "SUCCESS")
                Result.Success(children)
            } catch (e: Exception) {
                Log.e("GetChildrenLevelsUseCase", "ERROR: ${e.message}", e)
                Result.Error(
                    message = e.message ?: "Failed to load children levels",
                    throwable = e,
                )
            }
        }
    }
