package com.ih.osm.data.model

import com.ih.osm.domain.model.Card

data class GetCardsResponse(
    val data: GetPaginatedCardsResponse,
    val status: Int,
    val message: String,
)

data class GetPaginatedCardsResponse(
    val data: List<Card>,
    val totalPages: Int? = 0,
    val page: Int? = 0,
    val hasMore: Boolean = false,
    val limit: Int? = 0,
)

fun GetCardsResponse.toDomain() = this.data
