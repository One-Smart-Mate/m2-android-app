package com.ih.osm.domain.model

/**
 * Domain model for level tree lazy loading data
 *
 * This model represents a portion of the level tree with nested children up to a specified depth.
 *
 * @property data List of levels with nested children
 * @property parentId The parent level ID (null for root levels)
 * @property depth The depth of nesting included
 */
data class LevelTreeData(
    val data: List<Level>,
    val parentId: String?,
    val depth: Int,
)
