package com.ih.osm.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.osm.data.database.entities.priority.PriorityEntity

data class Priority(
    val id: String,
    @SerializedName("priorityCode")
    val code: String,
    @SerializedName("priorityDescription")
    val description: String,
    @SerializedName("priorityDays")
    val days: Int,
    val status: String,
)

fun Priority.toEntity(): PriorityEntity =
    PriorityEntity(
        id = this.id,
        code = this.code,
        description = this.description,
        days = this.days,
        status = this.status,
    )

fun List<Priority>.toNodeItemCard(): List<NodeCardItem> =
    this.map {
        NodeCardItem(id = it.id, name = it.code, description = it.description)
    }
