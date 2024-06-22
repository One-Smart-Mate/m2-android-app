package com.ih.m2.data.database.dao.card

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.card.CardEntity

@Dao
interface CardDao {

    @Query("SELECT * FROM card_table")
    suspend fun getCards(): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardEntity: CardEntity): Long

    @Query("DELETE FROM card_table")
    suspend fun deleteCards()

    @Query("SELECT site_card_id FROM card_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastSiteCardId(): Long


    @Query("SELECT id FROM card_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastCardId(): String

    @Query("SELECT * FROM card_table WHERE stored=:stored")
    suspend fun getLocalCards(stored: String): List<CardEntity>

}