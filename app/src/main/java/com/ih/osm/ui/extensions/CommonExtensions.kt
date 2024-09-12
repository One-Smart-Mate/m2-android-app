package com.ih.osm.ui.extensions

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import com.ih.osm.MainActivity

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
        this.getActivity<MainActivity>()?.workRequest()
    }
}
