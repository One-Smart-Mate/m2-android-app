package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName

data class Priority(
    val id: String,
    @SerializedName("priorityCode")
    val code: String,
    @SerializedName("priorityDescription")
    val description: String,
    @SerializedName("priorityDays")
    val days: Int,
    val status: String
)

fun List<Priority>.toNodeItemCard(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(id = it.id, name = it.code, description = it.description)
    }
}