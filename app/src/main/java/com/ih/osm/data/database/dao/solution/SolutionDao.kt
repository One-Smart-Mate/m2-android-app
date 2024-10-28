package com.ih.osm.data.database.dao.solution

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.solution.SolutionEntity

@Dao
interface SolutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solutionEntity: SolutionEntity): Long

    @Query("SELECT * FROM solution_table WHERE card_id=:uuid LIMIT 1")
    suspend fun getAllByCard(uuid: String): List<SolutionEntity>

    @Query("SELECT * FROM solution_table")
    suspend fun getAll(): List<SolutionEntity>

    @Query("DELETE FROM solution_table WHERE card_id=:uuid")
    suspend fun deleteAllByCard(uuid: String)

    @Query("DELETE FROM solution_table")
    suspend fun deleteAll()
}
