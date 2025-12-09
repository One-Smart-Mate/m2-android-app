package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.cache.LevelCacheManager
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface GetChildrenLevelsUseCase {
    suspend operator fun invoke(parentId: String): Result<List<Level>>
}

class GetChildrenLevelsUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        private val cacheManager: LevelCacheManager,
    ) : GetChildrenLevelsUseCase {
        override suspend fun invoke(parentId: String): Result<List<Level>> {
            return try {
                val cachedChildren = cacheManager.getCachedChildren(parentId)

                if (cachedChildren != null && cachedChildren.isNotEmpty()) {
                    return Result.Success(cachedChildren)
                }

                // if (parentId.isBlank()) {
                return Result.Error("Invalid parent ID: $parentId")
                // }

//                val children =
//                    levelRepository.getRemoteChildrenLevels(
//                        parentId = parentId,
//                        page = null,
//                        limit = null,
//                    )
//
//                if (children.isNotEmpty()) {
//                    cacheManager.cacheChildren(parentId, children)
//                }
//
//                Result.Success(children)
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Failed to load children levels",
                    throwable = e,
                )
            }
        }
    }
