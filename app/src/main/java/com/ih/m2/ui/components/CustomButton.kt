package com.ih.m2.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.m2.ui.pages.login.LoginScreen
import com.ih.m2.ui.theme.M2androidappTheme

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

        else -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }
    }

    val buttonModifier = when (buttonType) {
        ButtonType.DEFAULT -> {
            modifier
                .fillMaxWidth()
                .height(56.dp)
        }

        else -> {
            modifier
                .fillMaxWidth()
                .height(56.dp)
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
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(text)
        }
    }
}

enum class ButtonType {
    DEFAULT, OUTLINE
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomButtonPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Column {
                CustomSpacer()
                CustomButton(text = "Default") {

                }
                CustomSpacer()
                CustomButton(text = "Outline", buttonType = ButtonType.OUTLINE) {

                }
            }

        }
    }
}
