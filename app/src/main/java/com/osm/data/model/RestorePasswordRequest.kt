package com.osm.data.model

import com.osm.ui.utils.EMPTY

data class RestorePasswordRequest(
    val email: String = EMPTY,
    val newPassword: String? = null,
    val resetCode: String? = null
)