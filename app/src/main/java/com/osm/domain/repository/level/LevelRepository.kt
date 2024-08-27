package com.osm.domain.repository.level

import com.osm.domain.model.Level

interface LevelRepository {
    suspend fun getLevels(siteId: String): List<Level>
}