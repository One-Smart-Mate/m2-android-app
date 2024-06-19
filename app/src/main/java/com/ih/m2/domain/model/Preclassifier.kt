package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName

data class Preclassifier (
    val id: String,
    @SerializedName("preclassifierCode")
    val code: String,
    @SerializedName("preclassifierDescription")
    val description: String,
)


fun List<Preclassifier>.toNodeItemCard(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(
            id = it.id,
            name = it.code,
            description = it.description
        )
    }
}