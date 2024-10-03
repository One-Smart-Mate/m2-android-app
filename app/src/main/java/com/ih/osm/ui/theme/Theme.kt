package com.ih.osm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0XFFadc6ff),
        secondary = Color(0XFFbfc6dc),
        tertiary = Color(0XFF715573),
        background = Color(0XFF121316),
        surface = Color(0XFF121316),
        onPrimary = Color(0XFF102f60),
        onSecondary = Color(0XFF293041),
        onTertiary = Color(0XFF402843),
        primaryContainer = Color(0XFF2b4678),
        secondaryContainer = Color(0XFF3f4759),
        tertiaryContainer = Color(0XFF583e5b),
        onBackground = Color(0XFFe3e2e6),
        onSurface = Color(0XFFe3e2e6)
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0XFF445e91),
        secondary = Color(0XFF575e71),
        tertiary = Color(0XFF715573),
        background = Color(0XFFfaf9fd),
        surface = Color(0XFFfaf9fd),
        onPrimary = Color(0XFFFFFFFF),
        onSecondary = Color(0XFFFFFFFF),
        onTertiary = Color(0XFFFFFFFF),
        primaryContainer = Color(0XFFd8e2ff),
        secondaryContainer = Color(0XFFdbe2f9),
        tertiaryContainer = Color(0XFFfbd7fc),
        onBackground = Color(0XFF1b1b1f),
        onSurface = Color(0XFF1b1b1f)
    )

@Composable
fun OsmAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
