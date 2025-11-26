package com.ih.osm.domain.model

/**
 * Domain model for level statistics
 *
 * This model represents statistical information about the level hierarchy in a site.
 *
 * @property totalLevels Total number of levels in the site
 * @property activeLevels Number of active (non-deleted) levels
 * @property inactiveLevels Number of inactive (deleted) levels
 * @property rootLevels Number of root-level items
 * @property maxDepth Maximum depth of the level hierarchy
 * @property performanceWarning Whether the level count exceeds performance thresholds
 */
data class LevelStats(
    val totalLevels: Int,
    val activeLevels: Int,
    val inactiveLevels: Int,
    val rootLevels: Int,
    val maxDepth: Int,
    val performanceWarning: Boolean,
)
