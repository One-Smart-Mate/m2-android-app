package com.ih.m2.data.model

import com.ih.m2.ui.utils.EMPTY

data class RestorePasswordRequest(
    val email: String = EMPTY,
    val newPassword: String? = null,
    val resetCode: String? = null
)