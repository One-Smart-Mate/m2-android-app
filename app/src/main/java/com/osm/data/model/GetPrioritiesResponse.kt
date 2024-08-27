package com.osm.data.model

import com.osm.domain.model.Priority


data class GetPrioritiesResponse(
    val data: List<Priority>,
    val status: Long,
    val message: String
)

fun GetPrioritiesResponse.toDomain() = this.data