package com.ih.osm.domain.repository.level

import com.ih.osm.domain.model.Level

interface LevelRepository {
    suspend fun getLevels(siteId: String): List<Level>
}