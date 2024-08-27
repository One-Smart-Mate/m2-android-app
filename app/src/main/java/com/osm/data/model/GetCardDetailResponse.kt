package com.osm.data.model


import com.osm.domain.model.Card
import com.osm.domain.model.Evidence
import com.osm.ui.extensions.ISO_FORMAT
import com.osm.ui.extensions.toFormatDate

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