package com.ih.osm.data.model

import com.ih.osm.domain.model.Level

/**
 * Paginated response wrapper for levels API endpoints
 *
 * This model represents the pagination metadata and data returned by paginated level endpoints.
 * All level endpoints that support pagination return responses in this format.
 *
 * @property data List of levels for the current page
 * @property total Total number of levels available across all pages
 * @property page Current page number
 * @property limit Number of items per page
 * @property totalPages Total number of pages available
 * @property hasMore Whether there are more pages available after the current one
 */
data class PaginatedLevelsResponse(
    val data: List<Level>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasMore: Boolean,
)

/**
 * Extension function to convert paginated response to domain model (just the data list)
 * Use this when you only need the levels list without pagination metadata
 */
fun PaginatedLevelsResponse.toDomain() = this.data
