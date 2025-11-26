package com.ih.osm.data.model

import com.ih.osm.domain.model.Level

/**
 * Response wrapper for finding a level by its machineId
 *
 * API returns:
 * {
 *   "data": {
 *     "level": {...},        // The found level
 *     "path": "string",      // Path string representation
 *     "hierarchy": [...]     // Array of levels from root to found level
 *   }
 * }
 *
 * @property data The response data containing level information
 */
data class GetLevelByMachineIdResponse(
    val data: LevelByMachineIdData?,
)

/**
 * Data class containing the level information
 *
 * @property level The level found by machineId
 * @property path Path string representation
 * @property hierarchy Array of levels from root to the found level (can be null or empty)
 */
data class LevelByMachineIdData(
    val level: Level?,
    val path: String?,
    val hierarchy: List<Level>?,
)

/**
 * Extension function to extract the hierarchy from the response
 * Returns empty list if data or hierarchy is null
 */
fun GetLevelByMachineIdResponse.toDomain(): List<Level> = this.data?.hierarchy ?: emptyList()
