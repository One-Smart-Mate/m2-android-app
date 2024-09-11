package com.ih.osm.data.model

import com.ih.osm.domain.model.CardType

data class GetCardTypesResponse (
    val data: List<CardType>,
    val status: Long,
    val message: String
)

fun GetCardTypesResponse.toDomain() = this.data
