package com.ih.m2.data.database.entities.preclassifier

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.m2.domain.model.Preclassifier


@Entity(tableName = "preclassifier_table")
data class PreclassifierEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "description")
    val description: String,
)

fun PreclassifierEntity.toDomain(): Preclassifier {
    return Preclassifier(
        id = this.id, code = this.code, description = this.description
    )
}
