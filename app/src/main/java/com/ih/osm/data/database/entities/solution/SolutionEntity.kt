package com.ih.osm.data.database.entities.solution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solution_table")
data class SolutionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "solution_type")
    val solutionType: String,
    @ColumnInfo(name = "card_id")
    val cardId: String,
    @ColumnInfo(name = "comments")
    val comments: String,
    @ColumnInfo(name = "user_solution_id")
    val userSolutionId: String,
)
