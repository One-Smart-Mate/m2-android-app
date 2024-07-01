package com.ih.m2.data.database.dao.preclassifier

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity
import com.ih.m2.data.database.entities.preclassifier.PreclassifierEntity


@Dao
interface PreclassifierDao {

    @Query("SELECT * FROM preclassifier_table")
    suspend fun getPreclassifiers(): List<PreclassifierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreclassifier(preclassifierEntity: PreclassifierEntity): Long

    @Query("DELETE FROM preclassifier_table")
    suspend fun deletePreclassifiers()


    @Query("SELECT * FROM preclassifier_table WHERE id=:id")
    suspend fun getPreclassifier(id: String?): PreclassifierEntity?
}