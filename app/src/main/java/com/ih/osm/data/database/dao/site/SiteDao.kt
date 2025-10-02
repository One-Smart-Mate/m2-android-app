package com.ih.osm.data.database.dao.site

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.site.SiteEntity

@Dao
interface SiteDao {
    @Query("SELECT * FROM site_table")
    suspend fun getSites(): List<SiteEntity>

    @Query("SELECT * FROM site_table WHERE is_current = 1 LIMIT 1")
    suspend fun getCurrentSite(): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSites(sites: List<SiteEntity>): List<Long>

    @Query("UPDATE site_table SET is_current = 0")
    suspend fun clearCurrentSite()

    @Query("UPDATE site_table SET is_current = 1 WHERE id = :siteId")
    suspend fun setCurrentSite(siteId: String)

    @Query("DELETE FROM site_table")
    suspend fun clearSites()
}
