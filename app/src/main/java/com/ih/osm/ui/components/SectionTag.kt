package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.Size115


@Composable
fun SectionTag(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isErrorEnabled: Boolean = false
) {
    if (value.isNotEmpty()) {
        Row(
            modifier = modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(Size115)
            )
            CustomSpacer(
                direction = SpacerDirection.HORIZONTAL,
                space = SpacerSize.TINY
            )
            CustomTag(title = value, tagSize = TagSize.SMALL, isErrorEnabled = isErrorEnabled)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SectionTagPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Column {
                SectionTag(title = "Custom Tag","valuee")
            }
        }
    }
}