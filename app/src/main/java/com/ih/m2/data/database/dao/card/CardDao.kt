package com.ih.m2.data.database.dao.card

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.card.CardEntity

@Dao
interface CardDao {

    @Query("SELECT * FROM CARD_TABLE")
    suspend fun getCards(): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardEntity: CardEntity): Long

    @Query("DELETE FROM card_table")
    suspend fun deleteCards()
}