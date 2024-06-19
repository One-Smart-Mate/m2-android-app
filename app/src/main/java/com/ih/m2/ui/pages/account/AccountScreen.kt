package com.ih.m2.ui.pages.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.R
import com.ih.m2.core.ui.functions.getContext
import com.ih.m2.core.ui.functions.openAppSettings
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getTextColor
import com.ih.m2.ui.extensions.scaffold
import com.ih.m2.ui.navigation.navigateToHome
import com.ih.m2.ui.navigation.navigateToLogin
import com.ih.m2.ui.pages.home.HomeScreen
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingLarge
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.PaddingTiny
import com.ih.m2.ui.theme.PaddingToolbar
import com.ih.m2.ui.theme.Size38

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = mavericksViewModel()
) {

    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    AccountContent(
        navController = navController,
        account = {},
        logout = {
            viewModel.process(AccountViewModel.Action.Logout)
        },
        getContext()
    )
    if (state.errorMessage.isNotEmpty()) {
        Toast.makeText(getContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
    }
    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.logout) {
                    navController.navigateToLogin()
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountContent(
    navController: NavController,
    account: () -> Unit,
    logout: () -> Unit,
    context: Context
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = stringResource(R.string.account))
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.information)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        account()
                        Toast.makeText(context, "Open info", Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.notifications)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        openAppSettings(context)
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.logout)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        logout()
                    }
                )
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun AccountPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            AccountContent(
                navController = rememberNavController(),
                account = {},
                logout = {},
                context = context
            )
        }
    }
}