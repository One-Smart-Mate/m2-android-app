package com.ih.osm.data.database.dao.solution

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.solution.SolutionEntity

@Dao
interface SolutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolution(solutionEntity: SolutionEntity): Long

    @Query("SELECT * FROM solution_table WHERE card_id=:cardId LIMIT 1")
    suspend fun getSolutions(cardId: String): List<SolutionEntity>

    @Query("SELECT * FROM solution_table")
    suspend fun getAllSolutions(): List<SolutionEntity>

    @Query("DELETE FROM solution_table WHERE card_id=:cardId")
    suspend fun deleteSolutionsByCard(cardId: String)

    @Query("DELETE FROM solution_table")
    suspend fun deleteSolutions()
}
