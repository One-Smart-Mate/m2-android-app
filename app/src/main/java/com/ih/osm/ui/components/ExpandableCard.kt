package com.ih.osm.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val isExpanded =
        remember {
            mutableStateOf(expanded)
        }

    val arrowIcon =
        if (isExpanded.value) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }

    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(PaddingNormal)
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded.value = isExpanded.value.not()
                }
                .padding(PaddingNormal),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title)
            Icon(arrowIcon, contentDescription = title)
        }
        AnimatedVisibility(visible = isExpanded.value) {
            Column(
                modifier = Modifier.padding(PaddingNormal)
            ) {
                content()
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
private fun PreviewListSection() {
    OsmAppTheme {
        Surface {
            Column {
                ExpandableCard("Information") {
                    Text(text = "Content")
                }
                CustomSpacer()
                ExpandableCard("Information2 ") {
                }
            }
        }
    }
}
