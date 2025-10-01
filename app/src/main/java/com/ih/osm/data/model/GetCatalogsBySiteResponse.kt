package com.ih.osm.data.model

import com.ih.osm.domain.model.Catalogs

data class GetCatalogsBySiteResponse(
    val data: Catalogs,
    val status: Int,
    val message: String,
)

fun GetCatalogsBySiteResponse.toDomain() = this.data
