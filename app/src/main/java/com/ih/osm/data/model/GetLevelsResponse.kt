package com.ih.osm.data.model

import com.ih.osm.domain.model.Level

data class GetLevelsResponse(val data: List<Level>, val status: Int, val message: String)

fun GetLevelsResponse.toDomain() = this.data