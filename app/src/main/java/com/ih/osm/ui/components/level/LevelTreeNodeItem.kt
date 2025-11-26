package com.ih.osm.ui.components.level

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ih.osm.domain.model.LevelTreeNode

/**
 * Composable component for rendering a single node in the level tree
 *
 * This component displays:
 * - Loading indicator when children are being fetched
 * - Circle icon (blue for branch, yellow for leaf, gray for placeholder)
 * - Node name with indentation based on depth
 * - Expand/collapse icon if node has children
 * - Placeholder "..." indicator for unloaded children
 *
 * Matches the ReadOnlyNodeElement component from the web app.
 *
 * @param node The tree node to display
 * @param depth Current depth level (for indentation)
 * @param isExpanded Whether this node is expanded
 * @param isLoading Whether children are currently loading
 * @param cardCount Number of cards for this level (optional)
 * @param onNodeClick Callback when node is clicked (for details)
 * @param onExpandClick Callback when expand/collapse is clicked
 */
@Composable
fun LevelTreeNodeItem(
    node: LevelTreeNode,
    depth: Int = 0,
    isExpanded: Boolean = false,
    isLoading: Boolean = false,
    cardCount: Int? = null,
    onNodeClick: (String) -> Unit = {},
    onExpandClick: (String) -> Unit = {},
) {
    val isPlaceholder = node.isPlaceholder()
    val isLeafNode = !node.hasChildren
    val indentationPadding = (depth * 24).dp

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = !isPlaceholder) {
                    if (node.hasChildren) {
                        onExpandClick(node.id)
                    } else {
                        onNodeClick(node.id)
                    }
                }.padding(
                    start = indentationPadding,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 16.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        // Node icon (circle or loading indicator)
        Box(
            modifier =
                Modifier
                    .size(30.dp)
                    .padding(end = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                isPlaceholder -> {
                    Box(
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                    )
                }
                else -> {
                    Box(
                        modifier =
                            Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLeafNode) {
                                        Color(0xFFFFFF00)
                                    } else {
                                        Color(0xFF145695)
                                    },
                                ),
                    )
                }
            }
        }

        // Node text (name + card count)
        Column(
            modifier = Modifier.weight(1f),
        ) {
            val displayText =
                buildString {
                    append(node.name)
                    if (!isPlaceholder && cardCount != null && cardCount > 0) {
                        append(" ($cardCount)")
                    }
                    if (isLoading) {
                        append(" ...")
                    }
                }

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (isLoading || isPlaceholder) {
                        Color.Gray
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                fontWeight =
                    if (depth == 0) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                fontSize = 14.sp,
            )

            // Show children count if available
            if (!isPlaceholder && node.hasChildren && node.childrenCount > 0) {
                Text(
                    text = "${node.childrenCount} children",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp,
                )
            }
        }

        // Expand/collapse icon
        if (node.hasChildren && !isPlaceholder) {
            Icon(
                imageVector =
                    if (isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
