package com.ih.osm.core.ui

sealed class LCE<out T>(private val value: T?) {
    fun invoke(): T? = value
    data object Loading : LCE<Nothing>(value = null)

    data class Success<out T>(val value: T) : LCE<T>(value = value)

    data class Fail<out T>(val error: String = "", val value: T? = null) : LCE<T>(value = value)
}
