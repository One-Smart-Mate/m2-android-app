package com.ih.osm.data.model

import com.ih.osm.domain.model.Level

/**
 * Response data for lazy loading level tree endpoint
 *
 * This model represents the level tree structure with nested children up to a specified depth.
 * Used by the /level/tree/:siteId/lazy endpoint.
 *
 * @property data List of levels with nested children up to the requested depth
 * @property parentId The parent level ID (null for root levels)
 * @property depth The depth of nesting included in the response
 */
data class LevelTreeLazyData(
    val data: List<Level>,
    val parentId: String?,
    val depth: Int,
)

/**
 * Wrapper response for level tree lazy loading endpoint
 *
 * @property data The level tree data
 * @property status HTTP status code
 * @property message Response message
 */
data class GetLevelTreeLazyResponse(
    val data: LevelTreeLazyData,
    val status: Int,
    val message: String,
)

/**
 * Extension function to extract just the levels list from the tree response
 */
fun GetLevelTreeLazyResponse.toDomain() = this.data.data

/**
 * Extension function to extract the full tree data with metadata
 */
fun GetLevelTreeLazyResponse.toTreeData() = this.data

/**
 * Extension function to convert data model to domain model
 */
fun LevelTreeLazyData.toDomainModel() =
    com.ih.osm.domain.model.LevelTreeData(
        data = this.data,
        parentId = this.parentId,
        depth = this.depth,
    )
