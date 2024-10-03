package com.ih.osm.data.model

import com.ih.osm.domain.model.Priority

data class GetPrioritiesResponse(
    val data: List<Priority>,
    val status: Long,
    val message: String
)

fun GetPrioritiesResponse.toDomain() = this.data
