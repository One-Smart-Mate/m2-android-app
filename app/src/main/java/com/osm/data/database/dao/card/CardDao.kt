package com.osm.data.database.dao.card

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.osm.data.database.entities.card.CardEntity

@Dao
interface CardDao {

    @Query("SELECT * FROM card_table")
    suspend fun getCards(): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardEntity: CardEntity): Long

    @Query("DELETE FROM card_table")
    suspend fun deleteCards()

    @Query("DELETE FROM card_table where stored=:stored")
    suspend fun deleteRemoteCards(stored: String)

    @Query("SELECT site_card_id FROM card_table ORDER BY site_card_id DESC LIMIT 1")
    suspend fun getLastSiteCardId(): Long?


    @Query("SELECT id FROM card_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastCardId(): String?

    @Query("SELECT * FROM card_table WHERE stored=:stored")
    suspend fun getLocalCards(stored: String): List<CardEntity>


    @Query("DELETE FROM card_table WHERE card_uuid=:id")
    suspend fun deleteCard(id: String)

    @Query("SELECT * FROM card_table WHERE id=:cardId")
    suspend fun getCard(cardId: String): CardEntity

    @Query("SELECT * FROM card_table  WHERE superior_id=:id AND site_id=:siteId")
    suspend fun getCardsZone(siteId: String,id: String): List<CardEntity>
}
