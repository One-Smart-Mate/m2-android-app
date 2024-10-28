package com.ih.osm.data.database.dao.cardtype

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.cardtype.CardTypeEntity

@Dao
interface CardTypeDao {
    @Query("SELECT * FROM card_type_table")
    suspend fun getAll(): List<CardTypeEntity>

    @Query("SELECT * FROM card_type_table WHERE card_type_methodology=:filter")
    suspend fun getByMethodology(filter: String): List<CardTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cardTypeEntity: CardTypeEntity): Long

    @Query("DELETE FROM card_type_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM card_type_table WHERE id=:id")
    suspend fun get(id: String?): CardTypeEntity?
}
