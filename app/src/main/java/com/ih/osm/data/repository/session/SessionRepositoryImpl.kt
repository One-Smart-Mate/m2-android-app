package com.ih.osm.data.repository.session

import com.ih.osm.data.database.dao.SessionDao
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.domain.model.Session
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.session.SessionRepository

class SessionRepositoryImpl(
    private val sessionDao: SessionDao,
) : SessionRepository {
    override suspend fun get(): Session? {
        val entity = sessionDao.getSession()
        return entity?.toDomain()
    }

    override suspend fun save(session: Session) {
        delete()
        sessionDao.insertSession(session.toEntity())
    }

    override suspend fun delete() {
        sessionDao.getSession()?.let {
            sessionDao.deleteSession(it)
        }
    }

    override suspend fun getSiteId(): String? {
        return get()?.siteId
    }
}
