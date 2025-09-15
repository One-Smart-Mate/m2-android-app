package com.ih.osm.data.model

import com.ih.osm.domain.model.Opl

data class GetOplsResponse(
    val data: List<Opl>,
    val status: Int,
    val message: String,
)

fun GetOplsResponse.toDomain() = this.data
