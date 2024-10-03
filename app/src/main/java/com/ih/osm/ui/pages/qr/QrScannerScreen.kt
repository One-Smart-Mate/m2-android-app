package com.ih.osm.ui.pages.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.QrCamera
import com.ih.osm.ui.extensions.getIconColor
import com.ih.osm.ui.navigation.navigateToCardList
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.PaddingToolbarVertical
import com.ih.osm.ui.theme.Size38
import kotlinx.coroutines.launch

@Composable
fun QrScannerScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var isLoading by remember {
        mutableStateOf(false)
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    if (isLoading) {
        LoadingScreen(text = stringResource(R.string.loading_data))
    } else {
        Box(
            Modifier.fillMaxSize()
        ) {
            QrCamera(
                onCameraError = {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = it
                        )
                    }
                },
                onClick = {
                    if (it.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            val id = "superiorId:$it"
                            navController.navigateToCardList(id)
                        }
                    } else {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = context.getString(R.string.valid_superior_id)
                            )
                        }
                    }
                }
            )
            Box(
                modifier =
                Modifier
                    .padding(vertical = PaddingToolbarVertical, horizontal = PaddingNormal)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.empty),
                    modifier =
                    Modifier
                        .size(Size38)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = getIconColor()
                )
            }
        }
        SnackbarHost(hostState = snackBarHostState) {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White,
                modifier = Modifier.padding(top = PaddingToolbar)
            )
        }
    }
}
