package com.ih.m2.core

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