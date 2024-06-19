package com.ih.m2.data.database.dao.cardtype

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity

@Dao
interface CardTypeDao {

    @Query("SELECT * FROM card_type_table")
    suspend fun getCardTypes(): List<CardTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardType(cardTypeEntity: CardTypeEntity): Long

    @Query("DELETE FROM card_type_table")
    suspend fun deleteCardTypes()
}