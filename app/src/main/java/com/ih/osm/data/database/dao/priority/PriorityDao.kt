package com.ih.osm.data.database.dao.priority

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.priority.PriorityEntity

@Dao
interface PriorityDao {
    @Query("SELECT * FROM priority_table")
    suspend fun getAll(): List<PriorityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(priorityEntity: PriorityEntity): Long

    @Query("DELETE FROM priority_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM priority_table WHERE id=:id")
    suspend fun get(id: String): PriorityEntity?
}
