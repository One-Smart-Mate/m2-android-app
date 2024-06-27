package com.ih.m2.core.ui.functions

import org.json.JSONObject


fun customError(message: String): Nothing = throw CustomException(message)


class CustomException (
    override val message: String
) : Exception() {
    override fun getLocalizedMessage(): String {
        return message
    }
}