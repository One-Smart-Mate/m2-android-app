package com.ih.m2.data.model

import com.ih.m2.domain.model.Card

data class SolutionResponse(val data: Card, val status: Int, val message: String)

fun SolutionResponse.toDomain() = this.data