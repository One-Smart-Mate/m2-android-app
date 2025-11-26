package com.ih.osm.data.model

/**
 * Wrapper response for paginated cards endpoints
 *
 * The backend wraps paginated data in a standard response structure.
 * This model represents that outer wrapper.
 *
 * @property data The paginated cards data with metadata
 * @property status HTTP status code
 * @property message Response message
 */
data class GetPaginatedCardsResponse(
    val data: PaginatedCardsResponse,
    val status: Int,
    val message: String,
)

/**
 * Extension function to extract just the card list from the full response
 */
fun GetPaginatedCardsResponse.toDomain() = this.data.data

/**
 * Extension function to extract the full pagination metadata
 */
fun GetPaginatedCardsResponse.toPaginatedDomain() = this.data
