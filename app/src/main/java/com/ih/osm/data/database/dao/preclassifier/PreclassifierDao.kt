package com.ih.osm.data.database.dao.preclassifier

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.preclassifier.PreclassifierEntity

@Dao
interface PreclassifierDao {
    @Query("SELECT * FROM preclassifier_table")
    suspend fun getAll(): List<PreclassifierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preclassifierEntity: PreclassifierEntity): Long

    @Query("DELETE FROM preclassifier_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM preclassifier_table WHERE id=:id")
    suspend fun get(id: String): PreclassifierEntity?
}
