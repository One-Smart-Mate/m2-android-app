package com.osm.data.database.dao.evidence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.osm.data.database.entities.evidence.EvidenceEntity

@Dao
interface EvidenceDao {

    @Query("SELECT * FROM evidence_table WHERE card_id=:id")
    suspend fun getEvidencesByCard(id: String): List<EvidenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidenceEntity: EvidenceEntity): Long

    @Query("DELETE FROM evidence_table")
    suspend fun deleteEvidences()

    @Query("DELETE FROM evidence_table WHERE card_id=:id")
    suspend fun deleteEvidenceByCard(id: String)


    @Query("DELETE FROM evidence_table WHERE id=:id")
    suspend fun deleteEvidence(id: String)
}