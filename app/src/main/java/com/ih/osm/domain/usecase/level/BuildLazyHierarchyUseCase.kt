package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.LevelTreeNode
import com.ih.osm.domain.model.Result
import javax.inject.Inject

interface BuildLazyHierarchyUseCase {
    operator fun invoke(
        levels: List<Level>,
        loadedChildren: Map<String, List<Level>> = emptyMap(),
    ): Result<List<LevelTreeNode>>
}

class BuildLazyHierarchyUseCaseImpl
    @Inject
    constructor() : BuildLazyHierarchyUseCase {
        override fun invoke(
            levels: List<Level>,
            loadedChildren: Map<String, List<Level>>,
        ): Result<List<LevelTreeNode>> {
            return try {
                if (levels.isEmpty()) {
                    return Result.Success(emptyList())
                }

                val nodes = buildHierarchy(levels, loadedChildren)
                Result.Success(nodes)
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Failed to build hierarchy",
                    throwable = e,
                )
            }
        }

        private fun buildHierarchy(
            levels: List<Level>,
            loadedChildren: Map<String, List<Level>>,
        ): List<LevelTreeNode> {
            if (levels.isEmpty()) return emptyList()

            return levels.mapNotNull { level ->
                buildNode(level, loadedChildren)
            }
        }

        private fun buildNode(
            level: Level,
            loadedChildren: Map<String, List<Level>>,
        ): LevelTreeNode {
            val levelId = level.id
            val dynamicallyLoadedChildren = loadedChildren[levelId]
            val hasChildren = dynamicallyLoadedChildren != null && dynamicallyLoadedChildren.isNotEmpty()

            return when {
                dynamicallyLoadedChildren != null && dynamicallyLoadedChildren.isNotEmpty() -> {
                    val childNodes = buildHierarchy(dynamicallyLoadedChildren, loadedChildren)
                    LevelTreeNode(
                        level = level,
                        hasChildren = true,
                        childrenCount = dynamicallyLoadedChildren.size,
                        isLoaded = true,
                        children = childNodes,
                    )
                }

                hasChildren -> {
                    LevelTreeNode(
                        level = level,
                        hasChildren = true,
                        childrenCount = 0,
                        isLoaded = false,
                        children = listOf(LevelTreeNode.createPlaceholder(levelId)),
                    )
                }

                else -> {
                    LevelTreeNode(
                        level = level,
                        hasChildren = false,
                        childrenCount = 0,
                        isLoaded = true,
                        children = emptyList(),
                    )
                }
            }
        }
    }

fun List<Level>.toLazyTreeNodes(loadedChildren: Map<String, List<Level>> = emptyMap()): List<LevelTreeNode> {
    val useCase = BuildLazyHierarchyUseCaseImpl()
    return when (val result = useCase(this, loadedChildren)) {
        is Result.Success -> result.data
        else -> emptyList()
    }
}
