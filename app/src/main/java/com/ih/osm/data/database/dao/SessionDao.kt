package com.ih.osm.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.SessionEntity

interface SessionDao {
    @Query("SELECT * FROM SESSION_TABLE LIMIT 1")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Delete
    suspend fun deleteSession(session: SessionEntity): Int
}
