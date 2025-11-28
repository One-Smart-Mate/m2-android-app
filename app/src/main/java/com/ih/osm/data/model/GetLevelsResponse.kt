package com.ih.osm.data.model

import com.ih.osm.core.utils.PaginatedResponse
import com.ih.osm.domain.model.Level

data class GetLevelsResponse(
    val data: GetPaginatedLevelsResponse,
    val status: Int,
    val message: String,
)

data class GetPaginatedLevelsResponse(
    override val total: Int?,
    override val page: Int?,
    override val limit: Int?,
    override val totalPages: Int?,
    override val hasMore: Boolean?,
    override val data: List<Level>,
) : PaginatedResponse<Level>

fun GetLevelsResponse.toDomain() = this.data
