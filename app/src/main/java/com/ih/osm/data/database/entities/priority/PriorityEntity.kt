package com.ih.osm.data.database.entities.priority

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.domain.model.Priority

@Entity(tableName = "priority_table")
data class PriorityEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "days")
    val days: Int,
    @ColumnInfo(name = "status")
    val status: String
)

fun PriorityEntity.toDomain(): Priority {
    return Priority(
        id = this.id,
        code = this.code,
        description = this.description,
        days = this.days,
        status = this.status
    )
}
