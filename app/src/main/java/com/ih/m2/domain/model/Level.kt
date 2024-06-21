package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.data.database.entities.level.LevelEntity


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