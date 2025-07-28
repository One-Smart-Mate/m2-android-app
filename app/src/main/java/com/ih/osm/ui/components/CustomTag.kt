package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.osm.ui.extensions.getColor
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.OsmAppTheme

@Composable
fun CustomTag(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    title: String,
    tagSize: TagSize = TagSize.DEFAULT,
    tagType: TagType = TagType.DEFAULT,
    invertedColors: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    isErrorEnabled: Boolean = false,
) {
    val color =
        if (tagType == TagType.DEFAULT || invertedColors) {
            getColor()
        } else {
            getTextColor()
        }

    val tagTextStyle =
        when (tagSize) {
            TagSize.DEFAULT -> {
                MaterialTheme.typography.bodyMedium
                    .copy(
                        color = color,
                    )
            }

            TagSize.SMALL -> {
                MaterialTheme.typography.bodySmall
                    .copy(
                        color = color,
                    )
            }
        }

    val tagTypeModifier =
        when (tagType) {
            TagType.DEFAULT -> {
                val containerColor =
                    if (isErrorEnabled) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                modifier
                    .background(
                        color = containerColor,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            }

            TagType.OUTLINE -> {
                modifier
                    .border(
                        width = 1.dp,
                        color =
                            if (isErrorEnabled) {
                                MaterialTheme.colorScheme.error
                            } else if (invertedColors) {
                                getColor()
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(vertical = 2.dp, horizontal = 12.dp)
            }
        }

    Box(
        modifier = tagTypeModifier,
    ) {
        Text(
            text = title,
            style = tagTextStyle,
            modifier = textModifier,
            textAlign = textAlign,
            softWrap = true,
        )
    }
}

enum class TagSize {
    DEFAULT,
    SMALL,
}

enum class TagType {
    DEFAULT,
    OUTLINE,
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomTagPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Column {
                CustomTag(title = "Custom Tag")
                CustomSpacer()
                CustomTag(title = "Custom Tag Small", tagSize = TagSize.SMALL)
                CustomSpacer()
                CustomTag(title = "Custom Tag outline", tagType = TagType.OUTLINE)
                CustomSpacer()
                CustomTag(title = "Custom Tag outline", isErrorEnabled = true)
                CustomSpacer()
            }
        }
    }
}
