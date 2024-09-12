package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ih.osm.ui.extensions.getColor
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.utils.EMPTY

@Composable
fun LoadingScreen(text: String = EMPTY) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "scale",
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = getColor(),
            )
            CustomSpacer()
            Box {
                Text(
                    text = text,
                    modifier =
                        Modifier
                            .align(Alignment.Center),
                    // Text composable does not take TextMotion as a parameter.
                    // Provide it via style argument but make sure that we are copying from current theme
                    style =
                        LocalTextStyle.current.copy(
                            textMotion = TextMotion.Animated,
                            fontSize = 18.sp,
                            color = getColor(),
                        ),
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun ScreenLoadingPreview() {
    OsmAppTheme {
        Scaffold { _ ->
            LoadingScreen("Loading catalogs and cards...")
        }
    }
}
