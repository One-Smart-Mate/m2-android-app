package com.ih.osm.data.model

import com.ih.osm.domain.model.Level

/**
 * Wrapper response for level children endpoint
 *
 * API returns:
 * {
 *   "data": [...],  // Direct array of levels
 *   "status": 200,
 *   "message": "OK"
 * }
 *
 * @property data List of child levels (direct array)
 * @property status HTTP status code
 * @property message Response message
 */
data class GetLevelChildrenResponse(
    val data: List<Level>,
    val status: Int,
    val message: String,
)

/**
 * Extension function to extract just the levels list from the children response
 */
fun GetLevelChildrenResponse.toDomain() = this.data
