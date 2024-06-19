package com.ih.m2.data.model

import com.ih.m2.domain.model.CardType

data class GetCardTypesResponse (
    val data: List<CardType>,
    val status: Long,
    val message: String
)

fun GetCardTypesResponse.toDomain() = this.data
