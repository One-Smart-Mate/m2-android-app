package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingLarge
import com.ih.osm.ui.theme.Size38

@Composable
fun CustomAppBar(navController: NavController, title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.empty),
            modifier = Modifier
                .size(Size38)
                .clickable {
                    navController.popBackStack()
                },
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall
                .copy(color = getTextColor()),
            modifier = Modifier.padding(horizontal = PaddingLarge)
        )
        CustomSpacer()
    }
}

@Composable
fun CustomAppBar(
    navController: NavController,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.empty),
            modifier = Modifier
                .size(Size38)
                .clickable {
                    navController.popBackStack()
                },
        )
        content()
        CustomSpacer()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CustomAppBarPreview() {
    OsmAppTheme {
        Surface {
            CustomAppBar(rememberNavController(),"Account")
        }
    }
}
