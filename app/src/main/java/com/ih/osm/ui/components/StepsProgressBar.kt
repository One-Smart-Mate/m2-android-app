package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.osm.ui.extensions.getPrimaryColor
import com.ih.osm.ui.theme.OsmAppTheme

@Composable
fun StepsProgressBar(modifier: Modifier = Modifier, numberOfSteps: Int, currentStep: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 0..numberOfSteps) {
            Step(
                modifier = Modifier.weight(1F),
                isCompete = step < currentStep,
                isCurrent = step == currentStep
            )
        }
    }
}

@Composable
fun Step(modifier: Modifier = Modifier, isCompete: Boolean, isCurrent: Boolean) {
    val color = if (isCompete || isCurrent) getPrimaryColor() else Color.LightGray
    val innerCircleColor = if (isCompete) getPrimaryColor() else Color.LightGray

    Box(modifier = modifier) {
        // Line
        HorizontalDivider(
            modifier = Modifier.align(Alignment.CenterStart),
            color = color,
            thickness = 2.dp
        )

        // Circle
        Canvas(
            modifier =
            Modifier
                .size(15.dp)
                .align(Alignment.CenterEnd)
                .border(
                    shape = CircleShape,
                    width = 2.dp,
                    color = color
                ),
            onDraw = {
                drawCircle(color = innerCircleColor)
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun StepsProgressBarPreview() {
    OsmAppTheme {
        Surface {
            val currentStep = remember { mutableIntStateOf(1) }
            StepsProgressBar(
                modifier = Modifier.fillMaxWidth(),
                numberOfSteps = 5,
                currentStep = currentStep.value
            )
        }
    }
}
