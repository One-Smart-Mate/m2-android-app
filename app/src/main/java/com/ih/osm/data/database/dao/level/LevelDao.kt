package com.ih.osm.data.database.dao.level

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.level.LevelEntity

@Dao
interface LevelDao {
    @Query("SELECT * FROM level_table")
    suspend fun getAll(): List<LevelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(levelEntity: LevelEntity): Long

    @Query("DELETE FROM level_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM level_table WHERE id=:id")
    suspend fun get(id: String): LevelEntity?
}
