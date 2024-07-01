package com.ih.m2.data.database.dao.level

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity
import com.ih.m2.data.database.entities.level.LevelEntity


@Dao
interface LevelDao {

    @Query("SELECT * FROM level_table")
    suspend fun getLevels(): List<LevelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(levelEntity: LevelEntity): Long

    @Query("DELETE FROM level_table")
    suspend fun deleteLevels()

    @Query("SELECT * FROM level_table WHERE id=:id")
    suspend fun getLevel(id: String?): LevelEntity?


}