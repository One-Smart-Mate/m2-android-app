package com.ih.osm.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.osm.data.database.entities.level.LevelEntity

data class Level(
    val id: String,
    @SerializedName("responsibleId")
    val ownerId: String,
    @SerializedName("responsibleName")
    val ownerName: String,
    val superiorId: String,
    val name: String,
    val description: String,
    val status: String
)

fun Level.toEntity(): LevelEntity {
    return LevelEntity(
        id = this.id,
        ownerId = this.ownerId,
        ownerName = this.ownerName,
        superiorId = this.superiorId,
        name = this.name,
        description = this.description,
        status = this.status
    )
}

fun List<Level>.toNodeItemList(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(
            id = it.id,
            name = it.name,
            description = it.description,
            superiorId = it.superiorId
        )
    }
}
