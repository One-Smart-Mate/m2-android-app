package com.ih.osm.data.database.entities.level

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.domain.model.Level

@Entity(tableName = "level_table")
data class LevelEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    @ColumnInfo(name = "owner_name")
    val ownerName: String,
    @ColumnInfo(name = "superior_id")
    val superiorId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "status")
    val status: String,
)

fun LevelEntity.toDomain(): Level =
    Level(
        id = this.id,
        ownerId = this.ownerId,
        ownerName = this.ownerName,
        superiorId = this.superiorId,
        name = this.name,
        description = this.description,
        status = this.status,
    )
