package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.cache.LevelCacheManager
import com.ih.osm.domain.model.LevelTreeData
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface GetLevelTreeLazyUseCase {
    suspend operator fun invoke(depth: Int = 2): Result<LevelTreeData>
}

class GetLevelTreeLazyUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        private val cacheManager: LevelCacheManager,
    ) : GetLevelTreeLazyUseCase {
        override suspend fun invoke(depth: Int): Result<LevelTreeData> {
            return try {
                if (depth !in 1..10) {
                    return Result.Error("Depth must be between 1 and 10, got: $depth")
                }

//                val treeData =
//                    levelRepository.getRemoteLevelTreeLazy(
//                        page = null,
//                        limit = null,
//                        depth = depth,
//                    )
//
//                cacheManager.cacheTreeNode(
//                    parentId = treeData.parentId,
//                    depth = depth,
//                    levels = treeData.data,
//                )
//
//                Result.Success(treeData)
                Result.Error("Invalid parent ID: ")
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Failed to load level tree",
                    throwable = e,
                )
            }
        }
    }
