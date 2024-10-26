package com.ih.osm.core.workmanager

import java.util.UUID

object WorkManagerUUID {
    private var uuid: UUID? = null

    fun get(): UUID? {
        if (uuid == null) {
            uuid = UUID.randomUUID()
        }
        return uuid
    }

    fun resetUUID() {
        uuid = UUID.randomUUID()
    }
}
