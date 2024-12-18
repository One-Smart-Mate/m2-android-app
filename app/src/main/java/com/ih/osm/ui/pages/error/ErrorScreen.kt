package com.ih.osm.ui.pages.error

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.Size38
import com.ih.osm.ui.utils.EMPTY

@Composable
fun ErrorScreen(
    navController: NavController,
    errorMessage: String,
    onClick: () -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.error,
                )
                .padding(top = 36.dp, start = PaddingNormal, end = PaddingNormal),
    ) {
        item {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.empty),
                modifier =
                    Modifier
                        .size(Size38)
                        .clickable {
                            navController.popBackStack()
                        },
                tint = Color.White,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = EMPTY,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White,
                )
                CustomSpacer()
                Text(
                    text = "Ups! Something went wrong!",
                    style =
                        MaterialTheme.typography.titleLarge
                            .copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                )
                CustomSpacer()
                Text(
                    text = errorMessage,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                        ),
                )
                CustomSpacer()
                CustomButton(text = "Reload data", buttonType = ButtonType.ERROR) {
                    onClick()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun PreviewErrorScreen() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ErrorScreen(rememberNavController(), "Unable to load the dat") {
            }
        }
    }
}
