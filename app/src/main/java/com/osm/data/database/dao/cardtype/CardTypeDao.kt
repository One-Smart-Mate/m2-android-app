package com.osm.data.database.dao.cardtype

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.osm.data.database.entities.cardtype.CardTypeEntity

@Dao
interface CardTypeDao {

    @Query("SELECT * FROM card_type_table")
    suspend fun getCardTypes(): List<CardTypeEntity>

    @Query("SELECT * FROM card_type_table WHERE card_type_methodology=:filter")
    suspend fun getCardTypesByMethodology(filter: String): List<CardTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardType(cardTypeEntity: CardTypeEntity): Long

    @Query("DELETE FROM card_type_table")
    suspend fun deleteCardTypes()

    @Query("SELECT * FROM card_type_table WHERE id=:id")
    suspend fun getCardType(id: String?): CardTypeEntity?
}