package com.ih.osm.data.model

/**
 * Wrapper response for paginated levels endpoints
 *
 * The backend wraps paginated data in a standard response structure.
 * This model represents that outer wrapper.
 *
 * @property data The paginated levels data with metadata
 * @property status HTTP status code
 * @property message Response message
 */
data class GetPaginatedLevelsResponse(
    val data: PaginatedLevelsResponse,
    val status: Int,
    val message: String,
)

/**
 * Extension function to extract just the levels list from the full response
 */
fun GetPaginatedLevelsResponse.toDomain() = this.data.data

/**
 * Extension function to extract the full pagination metadata
 */
fun GetPaginatedLevelsResponse.toPaginatedDomain() = this.data
