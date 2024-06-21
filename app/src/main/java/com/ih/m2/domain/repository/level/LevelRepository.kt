package com.ih.m2.domain.repository.level

import com.ih.m2.domain.model.Level

interface LevelRepository {
    suspend fun getLevels(siteId: String): List<Level>
}