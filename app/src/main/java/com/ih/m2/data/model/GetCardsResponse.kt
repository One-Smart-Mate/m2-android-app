package com.ih.m2.data.model

import com.ih.m2.domain.model.Card

data class GetCardsResponse(val data: List<Card>,  val status: Int, val message: String)

fun GetCardsResponse.toDomain() = this.data