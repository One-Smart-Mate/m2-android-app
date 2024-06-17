package com.ih.m2.ui.pages.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import com.ih.m2.ui.components.CustomButton
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTextField
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.navigation.Screen
import com.ih.m2.ui.navigation.navigateAndClean
import com.ih.m2.ui.navigation.navigateToHome
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.Size150

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = mavericksViewModel(),
    navController: NavController
) {

    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LoginContent(
        email = state.email,
        password = state.password,
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
        }
    )

    if (state.errorMessage.isNotEmpty()) {
        Toast.makeText(LocalContext.current, state.errorMessage, Toast.LENGTH_LONG)
            .show()
    }
    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.isAuthenticated) {
                    navController.navigateToHome()
                }
            }
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isButtonLoading: Boolean,
    onLogin: () -> Unit
) {
    LazyColumn(
        modifier = modifier.background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            LoginTitle()
            Spacer(modifier = Modifier.fillMaxHeight())
            LoginForm(
                modifier = Modifier.fillParentMaxSize(),
                email = email,
                password = password,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                isButtonLoading = isButtonLoading,
                onLogin = onLogin
            )
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isButtonLoading: Boolean,
    onLogin: () -> Unit
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
                value = email,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                icon = Icons.Default.Email
            ) {
                onEmailChange(it)
            }
            CustomSpacer()
            CustomTextField(
                value = password,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                icon = Icons.Default.Person
            ) {
                onPasswordChange(it)

            }
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomButton(text = stringResource(R.string.login), isLoading = isButtonLoading) {
                onLogin()
            }
        }
    }
}

@Composable
fun LoginTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Size150),
        contentAlignment = Alignment.Center,

        ) {
        Text(
            text = stringResource(R.string.m2_app), style = MaterialTheme.typography.displayMedium
                .copy(color = getColor())
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun LoginPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                email = "test@gmial.com",
                password = "Password",
                onEmailChange = {},
                onPasswordChange = {},
                isButtonLoading = false,
                onLogin = {}
            )
        }
    }
}