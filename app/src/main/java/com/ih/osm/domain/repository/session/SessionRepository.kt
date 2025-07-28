package com.ih.osm.domain.repository.session

import com.ih.osm.domain.model.Session

interface SessionRepository {
    suspend fun save(session: Session)

    suspend fun get(): Session?

    suspend fun getSiteId(): String?

    suspend fun delete()
}
