package com.ih.m2.ui.pages.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.BuildConfig
import com.ih.m2.R
import com.ih.m2.core.ui.functions.getContext
import com.ih.m2.core.ui.functions.openAppSettings
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.ScreenLoading
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.navigation.Screen
import com.ih.m2.ui.navigation.navigateToLogin
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = mavericksViewModel()
) {

    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (state.isLoading) {
        ScreenLoading(stringResource(R.string.sync_catalogs))
    } else {
        AccountContent(
            navController = navController,
            onAccount = {},
            onLogout = {
                viewModel.process(AccountViewModel.Action.Logout)
            },
            onSyncCatalogs = {
                viewModel.process(AccountViewModel.Action.SyncCatalogs)
            },
            context = getContext(),
            onDevClick = {
                navController.navigate(Screen.Dev.route)
                //  viewModel.process(AccountViewModel.Action.ShowNotification)
            }
        )
    }
    if (state.message.isNotEmpty()) {
        Toast.makeText(getContext(), state.message, Toast.LENGTH_SHORT).show()
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
    onAccount: () -> Unit,
    onLogout: () -> Unit,
    onSyncCatalogs: () -> Unit,
    context: Context,
    onDevClick: () -> Unit
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = stringResource(R.string.account)
                )
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
                        onAccount()
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
                    headlineContent = { Text(text = stringResource(R.string.sync_remote_catalogs)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        onSyncCatalogs()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "Dev") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Build,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        onDevClick()
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
                        onLogout()
                    }
                )

                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                ListItem(
                    headlineContent = { Text("App Version ${BuildConfig.VERSION_NAME}") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal
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
                onAccount = {},
                onLogout = {},
                onSyncCatalogs = {},
                context,
                onDevClick = {}
            )
        }
    }
}