package com.ih.osm.ui.pages.password

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestoreAccountScreen(
    navController: NavController,
    viewModel: RestoreAccountViewModel = mavericksViewModel(),
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    RestoreAccountContent(
        navController = navController,
        isLoading = state.isLoading,
        currentStep = state.currentStep,
        onActionClick = {
            viewModel.process(RestoreAccountViewModel.Action.OnActionClick(it))
        },
        onEmailChange = {
            viewModel.process(RestoreAccountViewModel.Action.OnEmailChange(it))
        },
        onCodeChange = {
            viewModel.process(RestoreAccountViewModel.Action.OnCodeChange(it))
        },
        onPasswordChange = {
            viewModel.process(RestoreAccountViewModel.Action.OnPasswordChange(it))
        },
        onConfirmPasswordChange = {
            viewModel.process(RestoreAccountViewModel.Action.OnConfirmPasswordChange(it))
        },
        canResend = state.canResend,
    )

    if (state.message.isNotEmpty() && state.isLoading.not()) {
        scope.launch {
            snackBarHostState.showSnackbar(
                message = state.message,
            )
            viewModel.process(RestoreAccountViewModel.Action.ClearMessage)
        }
    }
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.isComplete) {
                    navController.popBackStack()
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RestoreAccountContent(
    navController: NavController,
    isLoading: Boolean,
    currentStep: Int,
    onEmailChange: (String) -> Unit,
    onActionClick: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    canResend: Boolean,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = "Restore account")
            }

            item {
                Column(
                    modifier = Modifier.padding(PaddingNormal),
                ) {
                    AnimatedVisibility(visible = currentStep == 1) {
                        VerifyEmailContent(
                            isLoading = isLoading,
                            onEmailChange = onEmailChange,
                            onActionClick = onActionClick,
                        )
                    }

                    AnimatedVisibility(visible = currentStep == 2) {
                        VerifyCodeContent(
                            isLoading = isLoading,
                            onActionClick = onActionClick,
                            onCodeChange = onCodeChange,
                            canResend = canResend,
                        )
                    }

                    AnimatedVisibility(visible = currentStep == 3) {
                        ChangePasswordContent(
                            isLoading = isLoading,
                            onActionClick = onActionClick,
                            onPasswordChange = onPasswordChange,
                            onConfirmPasswordChange = onConfirmPasswordChange,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifyEmailContent(
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onActionClick: (String) -> Unit,
) {
    Column {
        Text(text = "Enter your email to reset your password, we'll send you a code to reset your password.")
        CustomSpacer()
        CustomTextField(
            label = "Email",
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onEmailChange(it)
        }
        CustomSpacer()
        CustomButton(text = "Verify email", isLoading = isLoading) {
            onActionClick("email_check")
        }
    }
}

@Composable
private fun VerifyCodeContent(
    isLoading: Boolean,
    onActionClick: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    canResend: Boolean,
) {
    Column {
        Text(text = "Enter the verification code from your email")
        CustomSpacer()
        CustomTextField(
            label = "Code",
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onCodeChange(it)
        }
        CustomSpacer()
        CustomButton(text = "Verify Code", isLoading = isLoading) {
            onActionClick("code_check")
        }
        CustomSpacer()
        AnimatedVisibility(visible = isLoading.not() && canResend) {
            CustomButton(text = "Resend code", buttonType = ButtonType.OUTLINE) {
                onActionClick("resend_check")
            }
        }
    }
}

@Composable
private fun ChangePasswordContent(
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onActionClick: (String) -> Unit,
) {
    Column {
        Text(text = "Enter your new password")
        CustomSpacer()
        CustomTextField(
            label = "New Password",
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onPasswordChange(it)
        }
        CustomSpacer()
        CustomTextField(
            label = "Confirm password",
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onConfirmPasswordChange(it)
        }
        CustomSpacer()
        CustomButton(text = "Confirm", isLoading = isLoading) {
            onActionClick("password_check")
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
            RestoreAccountContent(
                navController = rememberNavController(),
                isLoading = false,
                currentStep = 1,
                onActionClick = {},
                onEmailChange = {},
                onCodeChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                canResend = true,
            )
        }
    }
}
