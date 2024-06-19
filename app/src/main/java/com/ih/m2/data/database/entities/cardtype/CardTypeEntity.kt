package com.ih.m2.data.database.entities.cardtype

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.m2.domain.model.CardType

@Entity(tableName = "card_type_table")
data class CardTypeEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "methodology")
    val methodology: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "color")
    val color: String,
    @ColumnInfo(name = "owner")
    val owner: String,
    @ColumnInfo(name = "status")
    val status: String
)

fun CardTypeEntity.toDomain(): CardType {
    return CardType(
        id = this.id,
        methodology = this.methodology,
        name = this.name,
        description = this.description,
        color = this.color,
        owner = this.owner,
        status = this.status
    )
}
