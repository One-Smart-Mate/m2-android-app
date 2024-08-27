package com.osm.data.model

import com.osm.domain.model.Level

data class GetLevelsResponse(val data: List<Level>, val status: Int, val message: String)

fun GetLevelsResponse.toDomain() = this.data