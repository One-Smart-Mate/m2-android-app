package com.ih.osm.data.model

import com.ih.osm.domain.model.Card

/**
 * Paginated response wrapper for cards API endpoints
 *
 * This model represents the pagination metadata and data returned by paginated card endpoints.
 * All card endpoints that support pagination return responses in this format.
 *
 * @property data List of cards for the current page
 * @property total Total number of cards available across all pages
 * @property page Current page number
 * @property limit Number of items per page
 * @property totalPages Total number of pages available
 * @property hasMore Whether there are more pages available after the current one
 */
data class PaginatedCardsResponse(
    val data: List<Card>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasMore: Boolean,
)

/**
 * Extension function to convert paginated response to domain model (just the data list)
 * Use this when you only need the cards list without pagination metadata
 */
fun PaginatedCardsResponse.toDomain() = this.data
