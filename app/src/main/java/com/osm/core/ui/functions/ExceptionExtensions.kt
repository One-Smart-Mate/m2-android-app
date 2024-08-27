package com.osm.core.ui.functions



fun customError(message: String): Nothing = throw CustomException(message)


class CustomException (
    override val message: String
) : Exception() {
    override fun getLocalizedMessage(): String {
        return message
    }
}