package com.ih.m2.ui.extensions

fun <T> T?.defaultIfNull(default: T): T = this ?: default
