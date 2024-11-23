package com.ih.osm.ui.pages.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ih.osm.BuildConfig
import com.ih.osm.R
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.extensions.getColor
import com.ih.osm.ui.navigation.navigateToHome
import com.ih.osm.ui.navigation.navigateToRestoreAccount
import com.ih.osm.ui.pages.login.action.LoginAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.PaddingToolbarVertical
import com.ih.osm.ui.theme.Size230
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LoginContent(
        isButtonLoading = state.isLoading,
        onAction = { action ->
            viewModel.process(action)
        },
        navController = navController
    )

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }
    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                if (it.isAuthenticated) {
                    navController.navigateToHome()
                }
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message
                        )
                    }
                    viewModel.cleanMessage()
                }
            }
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onAction: (LoginAction) -> Unit,
    isButtonLoading: Boolean,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier.background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            LoginTitle()
            Spacer(modifier = Modifier.fillMaxHeight())
            LoginForm(
                modifier = Modifier.fillParentMaxSize(),
                onAction = onAction,
                isButtonLoading = isButtonLoading,
                navController = navController
            )
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier,
    onAction: (LoginAction) -> Unit,
    isButtonLoading: Boolean,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(PaddingNormal)
        ) {
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                icon = Icons.Outlined.Email
            ) {
                onAction(LoginAction.SetEmail(it))
            }
            CustomSpacer()
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                icon = Icons.Outlined.Lock,
                isPassword = true
            ) {
                onAction(LoginAction.SetPassword(it))
            }
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomButton(text = stringResource(R.string.login), isLoading = isButtonLoading) {
                onAction(LoginAction.Login)
            }
            CustomSpacer()
            CustomButton(
                text = stringResource(R.string.forgot_password),
                buttonType = ButtonType.TEXT
            ) {
                navController.navigateToRestoreAccount()
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoginTitle() {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(Size230),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = PaddingToolbarVertical, bottom = PaddingNormal)
        ) {
            GlideImage(model = R.mipmap.ic_launcher, contentDescription = EMPTY)
            Text(
                text = stringResource(R.string.app_simple_name),
                style =
                MaterialTheme.typography.displaySmall
                    .copy(color = getColor())
            )
            Text(
                text = stringResource(R.string.app_simple_name_desc),
                style =
                MaterialTheme.typography.bodySmall
                    .copy(color = getColor())
            )
            CustomSpacer()
            Text(
                stringResource(
                    R.string.app_version,
                    BuildConfig.VERSION_NAME
                ),
                style = MaterialTheme.typography.labelSmall
                    .copy(color = getColor())
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun LoginPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                onAction = {},
                isButtonLoading = false,
                navController = rememberNavController()
            )
        }
    }
}
