package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.data.database.entities.preclassifier.PreclassifierEntity

data class Preclassifier (
    val id: String,
    @SerializedName("preclassifierCode")
    val code: String,
    @SerializedName("preclassifierDescription")
    val description: String,
    val cardTypeId: String,
)

fun Preclassifier.toEntity(): PreclassifierEntity {
    return PreclassifierEntity(
        id = this.id, code = this.code, description = this.description, cardTypeId = this.cardTypeId
    )
}



fun List<Preclassifier>.toNodeItemCard(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(
            id = it.id,
            name = it.code,
            description = it.description,
            superiorId = it.cardTypeId
        )
    }
}