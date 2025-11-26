package com.ih.osm.ui.pages.level

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.LevelStats
import com.ih.osm.domain.model.LevelTreeNode
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.usecase.level.BuildLazyHierarchyUseCase
import com.ih.osm.domain.usecase.level.GetChildrenLevelsUseCase
import com.ih.osm.domain.usecase.level.GetLevelStatsUseCase
import com.ih.osm.domain.usecase.level.GetLevelTreeLazyUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for lazy-loaded level tree view
 *
 * Manages the state of a hierarchical level tree with on-demand loading of children.
 * Follows the pattern from the web app's LevelReadOnlyLazy component.
 *
 * Key features:
 * - Loads initial tree with depth=2
 * - Tracks loading progress (0-100%)
 * - Loads children on-demand when nodes expand
 * - Prevents duplicate loading requests
 * - Rebuilds hierarchy when new children are loaded
 * - Caches loaded children for performance
 */
@HiltViewModel
class LevelTreeLazyViewModel
    @Inject
    constructor(
        private val getLevelTreeLazyUseCase: GetLevelTreeLazyUseCase,
        private val getChildrenLevelsUseCase: GetChildrenLevelsUseCase,
        private val buildLazyHierarchyUseCase: BuildLazyHierarchyUseCase,
        private val getLevelStatsUseCase: GetLevelStatsUseCase,
    ) : BaseViewModel<LevelTreeLazyViewModel.UiState>(UiState()) {
        /**
         * UI state for the level tree screen
         *
         * @property tree Hierarchical tree structure with nested children
         * @property originalData Original flat list of levels (for rebuilding)
         * @property loadingNodes Set of node IDs currently loading children
         * @property loadedChildren Map of parent ID to loaded children
         * @property expandedNodes Set of node IDs that are expanded
         * @property isLoading Whether initial load is in progress
         * @property progress Loading progress (0-100)
         * @property errorMessage Error message if load fails
         * @property stats Level statistics
         */
        data class UiState(
            val tree: List<LevelTreeNode> = emptyList(),
            val originalData: List<Level> = emptyList(),
            val loadingNodes: Set<String> = emptySet(),
            val loadedChildren: Map<String, List<Level>> = emptyMap(),
            val expandedNodes: Set<String> = emptySet(),
            val isLoading: Boolean = false,
            val progress: Int = 0,
            val errorMessage: String? = null,
            val stats: LevelStats? = null,
        )

        /**
         * Loads the initial level tree with progress tracking
         *
         * This mirrors the web app's handleGetLevels function:
         * 1. Shows progress 0-100%
         * 2. Fetches stats (10-30%)
         * 3. Fetches tree with depth=2 (30-50%)
         * 4. Builds hierarchy (50-90%)
         * 5. Completes (100%)
         */
        fun loadInitialTree() {
            Log.d("LevelTreeLazyViewModel", "===== LOAD INITIAL TREE START =====")
            viewModelScope.launch {
                setState { copy(isLoading = true, progress = 10, errorMessage = null) }
                Log.d("LevelTreeLazyViewModel", "State: isLoading=true, progress=10")

                try {
                    // Step 1: Get statistics (10-30%)
                    Log.d("LevelTreeLazyViewModel", "Step 1: Getting stats...")
                    val statsResult = callUseCase { getLevelStatsUseCase() }
                    when (statsResult) {
                        is Result.Success -> {
                            Log.d("LevelTreeLazyViewModel", "Stats SUCCESS: totalLevels=${statsResult.data.totalLevels}")
                            setState { copy(stats = statsResult.data, progress = 30) }
                        }
                        is Result.Error -> {
                            Log.e("LevelTreeLazyViewModel", "Stats ERROR: ${statsResult.message}")
                            setState {
                                copy(
                                    isLoading = false,
                                    progress = 0,
                                    errorMessage = statsResult.message,
                                )
                            }
                            return@launch
                        }
                        is Result.Loading -> { /* Handled by isLoading flag */ }
                    }

                    // Step 2: Get initial tree with depth=2 (30-50%)
                    Log.d("LevelTreeLazyViewModel", "Step 2: Getting tree with depth=2...")
                    val treeResult = callUseCase { getLevelTreeLazyUseCase(depth = 2) }
                    when (treeResult) {
                        is Result.Success -> {
                            val treeData = treeResult.data
                            Log.d("LevelTreeLazyViewModel", "Tree SUCCESS: ${treeData.data.size} root levels")
                            setState {
                                copy(
                                    originalData = treeData.data,
                                    progress = 50,
                                )
                            }
                        }
                        is Result.Error -> {
                            Log.e("LevelTreeLazyViewModel", "Tree ERROR: ${treeResult.message}")
                            setState {
                                copy(
                                    isLoading = false,
                                    progress = 0,
                                    errorMessage = treeResult.message,
                                )
                            }
                            return@launch
                        }
                        is Result.Loading -> { /* Handled by isLoading flag */ }
                    }

                    // Step 3: Build hierarchy (50-90%)
                    Log.d("LevelTreeLazyViewModel", "Step 3: Building hierarchy...")
                    val hierarchyResult =
                        buildLazyHierarchyUseCase(
                            getState().originalData,
                            emptyMap(),
                        )

                    when (hierarchyResult) {
                        is Result.Success -> {
                            Log.d("LevelTreeLazyViewModel", "Hierarchy SUCCESS: ${hierarchyResult.data.size} tree nodes")
                            setState {
                                copy(
                                    tree = hierarchyResult.data,
                                    progress = 90,
                                )
                            }
                        }
                        is Result.Error -> {
                            Log.e("LevelTreeLazyViewModel", "Hierarchy ERROR: ${hierarchyResult.message}")
                            setState {
                                copy(
                                    isLoading = false,
                                    progress = 0,
                                    errorMessage = hierarchyResult.message,
                                )
                            }
                            return@launch
                        }
                        is Result.Loading -> { /* Not used in this UseCase */ }
                    }

                    // Step 4: Complete (100%)
                    Log.d("LevelTreeLazyViewModel", "Step 4: Complete!")
                    setState { copy(isLoading = false, progress = 100) }
                    Log.d("LevelTreeLazyViewModel", "===== LOAD INITIAL TREE END =====")
                } catch (e: Exception) {
                    Log.e("LevelTreeLazyViewModel", "EXCEPTION: ${e.message}", e)
                    setState {
                        copy(
                            isLoading = false,
                            progress = 0,
                            errorMessage = e.message ?: "Unknown error occurred",
                        )
                    }
                }
            }
        }

        /**
         * Loads children for a specific node
         *
         * This mirrors the web app's handleNodeToggle function:
         * - Checks if already loading (prevents duplicates)
         * - Fetches children from repository
         * - Updates loadedChildren map
         * - Rebuilds tree hierarchy
         *
         * @param nodeId The parent node ID to load children for
         */
        fun loadChildren(nodeId: String) {
            val state = getState()

            // Prevent duplicate requests
            if (state.loadingNodes.contains(nodeId)) {
                return
            }

            viewModelScope.launch {
                // Add to loading nodes
                setState { copy(loadingNodes = loadingNodes + nodeId) }

                try {
                    // Fetch children
                    val result = callUseCase { getChildrenLevelsUseCase(nodeId) }

                    when (result) {
                        is Result.Success -> {
                            val children = result.data

                            // Update loaded children map
                            val updatedLoadedChildren =
                                state.loadedChildren.toMutableMap().apply {
                                    put(nodeId, children)
                                }

                            // Rebuild hierarchy with new children
                            rebuildTreeWithLoadedChildren(updatedLoadedChildren)
                        }
                        is Result.Error -> {
                            setState {
                                copy(
                                    loadingNodes = loadingNodes - nodeId,
                                    errorMessage = result.message,
                                )
                            }
                        }
                        is Result.Loading -> { /* Handled by loading state */ }
                    }
                } catch (e: Exception) {
                    setState {
                        copy(
                            loadingNodes = loadingNodes - nodeId,
                            errorMessage = e.message ?: "Failed to load children",
                        )
                    }
                } finally {
                    // Remove from loading nodes
                    setState { copy(loadingNodes = loadingNodes - nodeId) }
                }
            }
        }

        /**
         * Rebuilds tree hierarchy with loaded children
         *
         * This mirrors the web app's useEffect that rebuilds when loadedChildren changes
         *
         * @param loadedChildren Updated map of loaded children
         */
        private fun rebuildTreeWithLoadedChildren(loadedChildren: Map<String, List<Level>>) {
            val state = getState()

            val hierarchyResult =
                buildLazyHierarchyUseCase(
                    state.originalData,
                    loadedChildren,
                )

            when (hierarchyResult) {
                is Result.Success -> {
                    setState {
                        copy(
                            tree = hierarchyResult.data,
                            loadedChildren = loadedChildren,
                        )
                    }
                }
                is Result.Error -> {
                    setState { copy(errorMessage = hierarchyResult.message) }
                }
                is Result.Loading -> { /* Not used in this UseCase */ }
            }
        }

        /**
         * Toggles node expansion state
         *
         * @param nodeId The node to expand/collapse
         */
        fun toggleNodeExpansion(nodeId: String) {
            val state = getState()
            val isCurrentlyExpanded = state.expandedNodes.contains(nodeId)

            if (isCurrentlyExpanded) {
                // Collapse node
                setState { copy(expandedNodes = expandedNodes - nodeId) }
            } else {
                // Expand node
                setState { copy(expandedNodes = expandedNodes + nodeId) }

                // Check if we need to load children
                val node = findNodeInTree(state.tree, nodeId)
                if (node?.needsChildrenLoading() == true) {
                    loadChildren(nodeId)
                }
            }
        }

        /**
         * Collapses all nodes in the tree
         */
        fun collapseAllNodes() {
            setState { copy(expandedNodes = emptySet()) }
        }

        /**
         * Clears error message
         */
        fun clearError() {
            setState { copy(errorMessage = null) }
        }

        /**
         * Helper function to find a node in the tree by ID
         *
         * @param tree List of tree nodes to search
         * @param nodeId Node ID to find
         * @return Found node or null
         */
        private fun findNodeInTree(
            tree: List<LevelTreeNode>,
            nodeId: String,
        ): LevelTreeNode? {
            for (node in tree) {
                if (node.id == nodeId) return node

                // Recursively search children
                if (node.children.isNotEmpty()) {
                    val found = findNodeInTree(node.children, nodeId)
                    if (found != null) return found
                }
            }
            return null
        }
    }
