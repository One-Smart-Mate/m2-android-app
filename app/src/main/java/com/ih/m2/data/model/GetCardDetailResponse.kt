package com.ih.m2.data.model


import com.ih.m2.data.database.entities.card.validateDate
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.Evidence
import com.ih.m2.ui.extensions.ISO_FORMAT
import com.ih.m2.ui.extensions.toFormatDate

data class GetCardDetailResponse(val data: CardWrapper, val status: Int, val message: String)

data class CardWrapper(
    val card: Card?,
    val evidences: List<Evidence>
)

fun GetCardDetailResponse.toDomain(): Card? {
    return this.data.card?.copy(
        evidences = this.data.evidences,
        creationDateFormatted = this.data.card.creationDate.toFormatDate(ISO_FORMAT)
    )
}