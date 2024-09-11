package com.ih.osm.data.model

import com.ih.osm.ui.utils.EMPTY

data class RestorePasswordRequest(
    val email: String = EMPTY,
    val newPassword: String? = null,
    val resetCode: String? = null
)