package com.ih.osm.data.model

import com.ih.osm.domain.model.Opl

data class GetOplByIdResponse(val data: Opl, val status: Int, val message: String)

fun GetOplByIdResponse.toDomain() = this.data
