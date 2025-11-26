package com.ih.osm.domain.usecase.level

import android.util.Log
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.LevelTreeNode
import com.ih.osm.domain.model.Result
import javax.inject.Inject

/**
 * UseCase for building a lazy-loaded tree hierarchy from level data
 *
 * This UseCase transforms flat lists of levels into a hierarchical tree structure
 * with support for lazy loading. It mirrors the buildLazyHierarchy function from
 * the web app.
 *
 * Business Logic:
 * - Builds tree nodes from Level data
 * - Merges dynamically loaded children from expandedNodes map
 * - Adds placeholders for unloaded children (hasChildren = true but not loaded)
 * - Recursively builds nested children
 * - Marks nodes as loaded or unloaded based on available data
 *
 * This is a pure function with no I/O operations.
 */
interface BuildLazyHierarchyUseCase {
    /**
     * Builds a lazy-loaded tree hierarchy
     *
     * @param levels The base levels to build the tree from
     * @param loadedChildren Map of parent ID to loaded children (from dynamic loading)
     * @return Result wrapping list of tree nodes
     */
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
            Log.d("BuildLazyHierarchyUseCase", "===== EXECUTE: ${levels.size} levels, ${loadedChildren.size} loaded =====")

            return try {
                if (levels.isEmpty()) {
                    Log.w("BuildLazyHierarchyUseCase", "Empty levels list")
                    return Result.Success(emptyList())
                }

                val nodes = buildHierarchy(levels, loadedChildren)
                Log.d("BuildLazyHierarchyUseCase", "SUCCESS: Built ${nodes.size} tree nodes")
                Result.Success(nodes)
            } catch (e: Exception) {
                Log.e("BuildLazyHierarchyUseCase", "ERROR: ${e.message}", e)
                Result.Error(
                    message = e.message ?: "Failed to build hierarchy",
                    throwable = e,
                )
            }
        }

        /**
         * Recursively builds the tree hierarchy
         *
         * @param levels List of levels at current depth
         * @param loadedChildren Map of dynamically loaded children
         * @return List of tree nodes with nested children
         */
        private fun buildHierarchy(
            levels: List<Level>,
            loadedChildren: Map<String, List<Level>>,
        ): List<LevelTreeNode> {
            if (levels.isEmpty()) return emptyList()

            return levels.mapNotNull { level ->
                buildNode(level, loadedChildren)
            }
        }

        /**
         * Builds a single tree node with its children
         *
         * Logic:
         * 1. Check if node has dynamically loaded children (from loadedChildren map)
         * 2. If yes, recursively build those children and mark as loaded
         * 3. If no but hasChildren flag exists, add placeholder and mark as unloaded
         * 4. Otherwise, mark as leaf node (no children)
         *
         * @param level The level to convert to tree node
         * @param loadedChildren Map of dynamically loaded children
         * @return Tree node with nested children or placeholders
         */
        private fun buildNode(
            level: Level,
            loadedChildren: Map<String, List<Level>>,
        ): LevelTreeNode {
            val levelId = level.id

            // Check if children were dynamically loaded for this node
            val dynamicallyLoadedChildren = loadedChildren[levelId]

            // Determine hasChildren flag
            // In a real scenario, this would come from backend or be inferred
            // For now, we check if there are dynamically loaded children
            val hasChildren = dynamicallyLoadedChildren != null && dynamicallyLoadedChildren.isNotEmpty()

            return when {
                // Case 1: Dynamically loaded children exist
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

                // Case 2: Has children but not loaded yet (need placeholder)
                hasChildren -> {
                    LevelTreeNode(
                        level = level,
                        hasChildren = true,
                        childrenCount = 0, // Unknown until loaded
                        isLoaded = false,
                        children = listOf(LevelTreeNode.createPlaceholder(levelId)),
                    )
                }

                // Case 3: Leaf node (no children)
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

/**
 * Extension function to build tree from LevelTreeData
 *
 * This is a convenience function for building tree from API response data.
 *
 * @param loadedChildren Map of dynamically loaded children (from user interactions)
 * @return List of tree nodes
 */
fun List<Level>.toLazyTreeNodes(loadedChildren: Map<String, List<Level>> = emptyMap()): List<LevelTreeNode> {
    val useCase = BuildLazyHierarchyUseCaseImpl()
    return when (val result = useCase(this, loadedChildren)) {
        is Result.Success -> result.data
        else -> emptyList()
    }
}
