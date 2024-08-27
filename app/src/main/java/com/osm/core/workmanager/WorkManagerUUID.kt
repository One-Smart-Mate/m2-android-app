package com.osm.core.workmanager

import java.util.UUID

object WorkManagerUUID {

    private var uuid: UUID? = null

    fun get(): UUID? {
        if (uuid == null) {
            uuid = UUID.randomUUID()
        }
        return uuid
    }
}