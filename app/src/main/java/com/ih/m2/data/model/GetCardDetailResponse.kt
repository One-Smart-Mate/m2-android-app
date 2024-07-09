package com.ih.m2.data.model


import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.Evidence

data class GetCardDetailResponse(val data: CardWrapper, val status: Int, val message: String)

data class CardWrapper(
    val card: Card?,
    val evidences: List<Evidence>
)

fun GetCardDetailResponse.toDomain(): Card? {
    return this.data.card?.copy(
        evidences = this.data.evidences
    )
}