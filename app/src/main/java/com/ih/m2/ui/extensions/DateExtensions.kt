package com.ih.m2.ui.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


val Date.YYYY_MM_DD_HH_MM_SS: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)