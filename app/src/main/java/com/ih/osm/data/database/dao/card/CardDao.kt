package com.ih.osm.data.database.dao.card

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.card.CardEntity

@Dao
interface CardDao {
    @Query("SELECT * FROM card_table")
    suspend fun getAll(): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cardEntity: CardEntity): Long

    @Query("DELETE FROM card_table")
    suspend fun deleteAll()

    @Query("DELETE FROM card_table where stored=:stored")
    suspend fun deleteRemotes(stored: String)

    @Query("SELECT site_card_id FROM card_table ORDER BY site_card_id DESC LIMIT 1")
    suspend fun getLastSiteCardId(): Long?

    @Query("SELECT card_id FROM card_table ORDER BY card_id DESC LIMIT 1")
    suspend fun getLastCardId(): String?

    @Query("SELECT * FROM card_table WHERE stored=:stored")
    suspend fun getAllLocal(stored: String): List<CardEntity>

    @Query("DELETE FROM card_table WHERE id=:uuid")
    suspend fun delete(uuid: String)

    @Query("SELECT * FROM card_table WHERE id=:uuid")
    suspend fun get(uuid: String): CardEntity?

    @Query("SELECT * FROM card_table  WHERE superior_id=:id AND site_id=:siteId")
    suspend fun getByZone(
        siteId: String,
        id: String,
    ): List<CardEntity>

    @Query("SELECT * FROM card_table ORDER BY site_card_id DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(
        limit: Int,
        offset: Int,
    ): List<CardEntity>

    @Query("SELECT COUNT(*) FROM card_table")
    suspend fun getCount(): Int
}
