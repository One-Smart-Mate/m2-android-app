package com.ih.osm.data.database.dao.evidence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.evidence.EvidenceEntity

@Dao
interface EvidenceDao {

    @Query("SELECT * FROM evidence_table WHERE card_id=:uuid")
    suspend fun getAllByCard(uuid: String): List<EvidenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evidenceEntity: EvidenceEntity): Long

    @Query("DELETE FROM evidence_table")
    suspend fun deleteAll()

    @Query("DELETE FROM evidence_table WHERE card_id=:uuid")
    suspend fun deleteByCard(uuid: String)

    @Query("DELETE FROM evidence_table WHERE id=:id")
    suspend fun delete(id: String)
}
