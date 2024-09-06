package com.osm.ui.pages.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.BuildConfig
import com.ih.osm.R
import com.osm.core.ui.functions.getContext
import com.osm.core.ui.functions.openAppSettings
import com.osm.ui.components.CustomAppBar
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.LoadingScreen
import com.osm.ui.components.SpacerSize
import com.osm.ui.extensions.defaultScreen
import com.osm.ui.navigation.Screen
import com.osm.ui.navigation.navigateToLogin
import com.osm.ui.navigation.navigateToProfile
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.theme.PaddingNormal

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = mavericksViewModel()
) {

    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (state.isLoading) {
        LoadingScreen(stringResource(R.string.sync_catalogs))
    } else {
        AccountContent(
            navController = navController,
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
            },
            checked = state.checked,
            onSwitchChange = {
                viewModel.process(AccountViewModel.Action.OnSwitchChange(it))
            },
            uri = state.uri
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
    onLogout: () -> Unit,
    onSyncCatalogs: () -> Unit,
    context: Context,
    onDevClick: () -> Unit,
    checked: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    uri: Uri? = null
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
                        navController.navigateToProfile()
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
                    headlineContent = { Text(text = stringResource(R.string.use_data_mobile)) },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_wifi),
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = checked, onCheckedChange = {
                                onSwitchChange(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        onSyncCatalogs()
                    }
                )

//                ListItem(
//                    headlineContent = { Text(text = "Dev") },
//                    leadingContent = {
//                        Icon(
//                            Icons.Filled.Build,
//                            contentDescription = stringResource(R.string.empty),
//                        )
//                    },
//                    tonalElevation = PaddingNormal,
//                    modifier = Modifier.clickable {
//                        onDevClick()
//                    }
//                )

                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(
                                R.string.app_version,
                                BuildConfig.VERSION_NAME
                            )
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal
                )
                AnimatedVisibility(visible = uri != null) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.share_app_logs)) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = stringResource(R.string.empty),
                            )
                        },
                        tonalElevation = PaddingNormal,
                        modifier = Modifier.clickable {
                            shareUri(context, uri)
                        }
                    )
                }
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                ListItem(
                    headlineContent = { Text(stringResource(R.string.logout)) },
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier = Modifier.clickable {
                        onLogout()
                    }
                )
            }
        }
    }
}

private fun shareUri(context: Context, uri: Uri?) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setType("*/*")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Can't share the data $uri",
            Toast.LENGTH_SHORT
        ).show()
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun AccountPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            AccountContent(
                navController = rememberNavController(),
                onLogout = {},
                onSyncCatalogs = {},
                context,
                onDevClick = {},
                onSwitchChange = {}, checked = true
            )
        }
    }
}