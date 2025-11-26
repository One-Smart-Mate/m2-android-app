package com.ih.osm.data.model

/**
 * Level statistics data
 *
 * This model contains statistical information about levels in a site.
 * Used by the /level/stats/:siteId endpoint.
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

/**
 * Response data wrapper for level stats
 *
 * @property stats The level statistics
 */
data class LevelStatsData(
    val stats: LevelStats,
)

/**
 * Wrapper response for level stats endpoint
 *
 * @property data The stats data wrapper
 * @property status HTTP status code
 * @property message Response message
 */
data class GetLevelStatsResponse(
    val data: LevelStatsData,
    val status: Int,
    val message: String,
)

/**
 * Extension function to extract the stats from the response
 */
fun GetLevelStatsResponse.toDomain() = this.data.stats

/**
 * Extension function to convert data model to domain model
 */
fun LevelStats.toDomainModel() =
    com.ih.osm.domain.model.LevelStats(
        totalLevels = this.totalLevels,
        activeLevels = this.activeLevels,
        inactiveLevels = this.inactiveLevels,
        rootLevels = this.rootLevels,
        maxDepth = this.maxDepth,
        performanceWarning = this.performanceWarning,
    )
