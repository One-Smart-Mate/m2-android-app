package com.ih.m2.ui.extensions

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.work.Operation
import com.ih.m2.MainActivity

fun <T> T?.defaultIfNull(default: T): T = this ?: default


inline fun <reified Activity : ComponentActivity> Context.getActivity(): Activity? {
    return when (this) {
        is Activity -> this
        else -> {
            var context = this
            while (context is ContextWrapper) {
                context = context.baseContext
                if (context is Activity) return context
            }
            null
        }
    }
}

fun Context.runWorkRequest() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.getActivity<MainActivity>()?.workRequest(this)
    }
}