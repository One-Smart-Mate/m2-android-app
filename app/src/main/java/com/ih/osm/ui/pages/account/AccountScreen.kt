package com.ih.osm.ui.pages.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.BuildConfig
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.ui.functions.openAppSettings
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.navigation.Screen
import com.ih.osm.ui.navigation.navigateToLogin
import com.ih.osm.ui.navigation.navigateToProfile
import com.ih.osm.ui.pages.account.action.AccountAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (state.isLoading) {
        LoadingScreen()
    } else {
        AccountContent(
            navController = navController,
            checked = state.checked,
            uri = state.uri,
            onAction = { action ->
                viewModel.process(action)
            },
        )
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .collect {
                if (it.logout) {
                    navController.navigateToLogin()
                }
                if (state.message.isNotEmpty()) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountContent(
    navController: NavController,
    checked: Boolean,
    uri: Uri? = null,
    onAction: (AccountAction) -> Unit,
) {
    val context = LocalContext.current
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = stringResource(R.string.account),
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
                    modifier =
                        Modifier.clickable {
                            navController.navigateToProfile()
                        },
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
                    modifier =
                        Modifier.clickable {
                            openAppSettings(context)
                        },
                )

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.sync_remote_catalogs),
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    modifier =
                        Modifier.clickable {
                            onAction(AccountAction.SyncCatalogs)
                        },
                )

                ListItem(
                    headlineContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.use_data_mobile), modifier = Modifier.weight(1f))
                            Switch(checked = checked, onCheckedChange = { isChecked ->
                                onAction(AccountAction.SetSwitch(isChecked))
                            })
                        }
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_wifi),
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                )

                if (BuildConfig.DEBUG) {
                    ListItem(
                        headlineContent = { Text(text = "Developer screen") },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.empty),
                            )
                        },
                        tonalElevation = PaddingNormal,
                        modifier =
                            Modifier.clickable {
                                navController.navigate(Screen.Dev.route)
                            },
                    )
                }

                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(
                                R.string.app_version,
                                BuildConfig.VERSION_NAME,
                            ),
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
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
                        modifier =
                            Modifier.clickable {
                                shareUri(context, uri)
                            },
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
                    modifier =
                        Modifier.clickable {
                            onAction(AccountAction.Logout)
                        },
                )
            }
        }
    }
}

private fun shareUri(
    context: Context,
    uri: Uri?,
) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setType("*/*")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        LoggerHelperManager.logException(e)
        Toast.makeText(
            context,
            "Can't share the data $uri",
            Toast.LENGTH_SHORT,
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
                checked = false,
                uri = null,
                onAction = {},
            )
        }
    }
}
