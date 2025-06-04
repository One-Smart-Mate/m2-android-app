package com.ih.osm.data.model

import com.ih.osm.domain.model.CiltData

data class GetCiltResponse(
    val data: CiltData,
)

fun GetCiltResponse.toDomain() = this.data
