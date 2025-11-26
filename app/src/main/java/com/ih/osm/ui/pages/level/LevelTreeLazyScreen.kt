package com.ih.osm.ui.pages.level

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.domain.model.LevelTreeNode
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.level.LevelTreeNodeItem
import com.ih.osm.ui.extensions.defaultScreen

/**
 * Screen for displaying lazy-loaded level tree
 *
 * Features:
 * - Shows progress bar during initial load (0-100%)
 * - Renders tree in LazyColumn for performance
 * - Supports expand/collapse for nodes with children
 * - Shows loading indicators for individual nodes
 * - Displays error messages in Snackbar
 * - Collapse All FAB for convenience
 *
 * Mirrors the LevelReadOnlyLazy component from web app
 */
@Composable
fun LevelTreeLazyScreen(
    navController: NavController,
    siteName: String? = null,
    viewModel: LevelTreeLazyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error messages in Snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Dismiss",
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
        floatingActionButton = {
            // Collapse All button (only show when tree is loaded)
            if (state.tree.isNotEmpty() && !state.isLoading) {
                FloatingActionButton(
                    onClick = { viewModel.collapseAllNodes() },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Collapse All",
                        tint = Color.White,
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.defaultScreen(padding),
        ) {
            // App bar
            CustomAppBar(
                navController = navController,
                title = "Levels: ${siteName ?: "Site"} (Read Only)",
            )

            // Loading progress indicator (only during initial load)
            if (state.isLoading && state.progress > 0 && state.progress < 100) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(
                                Color.White,
                                shape = MaterialTheme.shapes.small,
                            ),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Text(
                            text = "Loading tree... ${state.progress}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        LinearProgressIndicator(
                            progress = state.progress / 100f,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Statistics display
            state.stats?.let { stats ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Text(
                            text = "Level Statistics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = "Total: ${stats.totalLevels} | Active: ${stats.activeLevels} | Max Depth: ${stats.maxDepth}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 12.sp,
                        )
                        if (stats.performanceWarning) {
                            Text(
                                text = "Warning: Large hierarchy may impact performance",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }

            // Tree content
            when {
                state.isLoading && state.progress == 0 -> {
                    // Initial loading state (before progress starts)
                    LoadingScreen(text = "Loading level tree...")
                }
                state.tree.isEmpty() && !state.isLoading -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No levels found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                        )
                    }
                }
                else -> {
                    // Tree view
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        // Recursively render tree nodes
                        items(
                            items = state.tree,
                            key = { node -> node.id },
                        ) { node ->
                            RenderTreeNode(
                                node = node,
                                depth = 0,
                                expandedNodes = state.expandedNodes,
                                loadingNodes = state.loadingNodes,
                                onNodeClick = { nodeId ->
                                    // TODO: Navigate to node details or show drawer
                                },
                                onExpandClick = { nodeId ->
                                    viewModel.toggleNodeExpansion(nodeId)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recursively renders a tree node and its children
 *
 * This function handles the hierarchical rendering of nodes,
 * showing children only when the parent is expanded.
 *
 * @param node The tree node to render
 * @param depth Current depth level (for indentation)
 * @param expandedNodes Set of expanded node IDs
 * @param loadingNodes Set of loading node IDs
 * @param onNodeClick Callback when node is clicked
 * @param onExpandClick Callback when expand/collapse is clicked
 */
@Composable
private fun RenderTreeNode(
    node: LevelTreeNode,
    depth: Int,
    expandedNodes: Set<String>,
    loadingNodes: Set<String>,
    onNodeClick: (String) -> Unit,
    onExpandClick: (String) -> Unit,
) {
    val isExpanded = expandedNodes.contains(node.id)
    val isLoading = loadingNodes.contains(node.id)

    // Render this node
    LevelTreeNodeItem(
        node = node,
        depth = depth,
        isExpanded = isExpanded,
        isLoading = isLoading,
        cardCount = null, // TODO: Add card count support
        onNodeClick = onNodeClick,
        onExpandClick = onExpandClick,
    )

    // Render children if expanded
    if (isExpanded && node.children.isNotEmpty()) {
        node.children.forEach { childNode ->
            RenderTreeNode(
                node = childNode,
                depth = depth + 1,
                expandedNodes = expandedNodes,
                loadingNodes = loadingNodes,
                onNodeClick = onNodeClick,
                onExpandClick = onExpandClick,
            )
        }
    }
}
