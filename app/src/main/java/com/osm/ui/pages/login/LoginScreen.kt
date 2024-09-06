package com.osm.ui.pages.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ih.osm.R
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.CustomTextField
import com.osm.ui.components.SpacerSize
import com.osm.ui.components.buttons.ButtonType
import com.osm.ui.components.buttons.CustomButton
import com.osm.ui.extensions.getColor
import com.osm.ui.navigation.navigateToHome
import com.osm.ui.navigation.navigateToHomeV2
import com.osm.ui.navigation.navigateToRestoreAccount
import com.osm.ui.pages.home.HomeViewModelV2
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.theme.PaddingToolbar
import com.osm.ui.theme.PaddingToolbarVertical
import com.osm.ui.theme.Size150
import com.osm.ui.theme.Size170
import com.osm.ui.theme.Size200
import com.osm.ui.utils.EMPTY
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = mavericksViewModel(),
    navController: NavController
) {

    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LoginContent(
        onEmailChange = {
            viewModel.process(LoginViewModel.Action.SetEmail(it))
        },
        onPasswordChange = {
            viewModel.process(LoginViewModel.Action.SetPassword(it))
        },
        isButtonLoading = state.isLoading,
        onLogin = {
            viewModel.process(
                LoginViewModel.Action.Login(
                    state.email,
                    state.password
                )
            )
        },
        onNavigateToPassword = {
            navController.navigateToRestoreAccount()
        }
    )

    if (state.message.isNotEmpty() && state.isLoading.not()) {
        scope.launch {
            snackBarHostState.showSnackbar(
                message = state.message,
            )
            viewModel.process(LoginViewModel.Action.ClearMessage)
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
    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.isAuthenticated) {
                    navController.navigateToHomeV2()
                }
            }
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isButtonLoading: Boolean,
    onLogin: () -> Unit,
    onNavigateToPassword: () -> Unit
) {
    LazyColumn(
        modifier = modifier.background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            LoginTitle()
            Spacer(modifier = Modifier.fillMaxHeight())
            LoginForm(
                modifier = Modifier.fillParentMaxSize(),
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                isButtonLoading = isButtonLoading,
                onLogin = onLogin,
                onNavigateToPassword = onNavigateToPassword
            )
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isButtonLoading: Boolean,
    onLogin: () -> Unit,
    onNavigateToPassword: () -> Unit
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
                onEmailChange(it)
            }
            CustomSpacer()
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                icon = Icons.Outlined.Lock,
                isPassword = true
            ) {
                onPasswordChange(it)

            }
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomButton(text = stringResource(R.string.login), isLoading = isButtonLoading) {
                onLogin()
            }
            CustomSpacer()
            CustomButton(text = "Forgot password?", buttonType = ButtonType.TEXT) {
                onNavigateToPassword()
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoginTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Size200),
        contentAlignment = Alignment.Center,

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = PaddingToolbarVertical, bottom = PaddingNormal)
        ) {
            GlideImage(model = R.mipmap.ic_launcher, contentDescription = EMPTY)
            Text(
                text = stringResource(R.string.app_simple_name), style = MaterialTheme.typography.displaySmall
                    .copy(color = getColor())

            )
            Text(
                text = stringResource(R.string.app_simple_name_desc), style = MaterialTheme.typography.bodySmall
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
                onEmailChange = {},
                onPasswordChange = {},
                isButtonLoading = false,
                onLogin = {},
                onNavigateToPassword = {}
            )
        }
    }
}