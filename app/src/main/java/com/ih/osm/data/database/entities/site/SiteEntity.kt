package com.ih.osm.data.database.entities.site

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.data.model.Site

@Entity(tableName = "site_table")
data class SiteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "logo")
    val logo: String,
    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean = false,
)

fun SiteEntity.toDomain(): Site =
    Site(
        id = this.id,
        name = this.name,
        logo = this.logo,
    )

fun Site.toEntity(isCurrent: Boolean = false): SiteEntity =
    SiteEntity(
        id = this.id,
        name = this.name,
        logo = this.logo,
        isCurrent = isCurrent,
    )
