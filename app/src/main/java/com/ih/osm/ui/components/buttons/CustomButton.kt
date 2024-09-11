package com.ih.osm.ui.components.buttons

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.theme.OsmAppTheme

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean = false,
    buttonType: ButtonType = ButtonType.DEFAULT,
    onClick: () -> Unit,
) {


    val colors = when (buttonType) {
        ButtonType.DEFAULT -> {
            ButtonDefaults.buttonColors()
        }

        ButtonType.ERROR -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        else -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }
    }

    val buttonModifier = when (buttonType) {
        ButtonType.DEFAULT, ButtonType.ERROR, ButtonType.TEXT -> {
            modifier
                .fillMaxWidth()
                .height(54.dp)
        }

        else -> {
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        }
    }

    Button(
        modifier = buttonModifier,
        onClick = onClick,
        colors = colors,
        enabled = isLoading.not()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(text)
        }
    }
}

enum class ButtonType {
    DEFAULT, OUTLINE, ERROR, TEXT
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomButtonPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Column {
                CustomSpacer()
                CustomButton(text = "Default") {

                }
                CustomSpacer()
                CustomButton(text = "Outline", buttonType = ButtonType.OUTLINE) {

                }
                CustomSpacer()
                CustomButton(text = "Error", buttonType = ButtonType.ERROR) {

                }

                CustomButton(text = "Text", buttonType = ButtonType.TEXT) {

                }
            }

        }
    }
}
