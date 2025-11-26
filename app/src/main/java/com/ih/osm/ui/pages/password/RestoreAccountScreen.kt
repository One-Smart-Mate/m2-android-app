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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.pages.password.action.RestoreAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestoreAccountScreen(
    navController: NavController,
    viewModel: RestoreAccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    RestoreAccountContent(
        navController = navController,
        isLoading = state.isLoading,
        currentStep = state.currentStep,
        onAction = { action ->
            viewModel.process(action)
        },
        canResend = state.canResend,
    )

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                if (it.isComplete) {
                    navController.popBackStack()
                }
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message,
                        )
                        viewModel.cleanMessage()
                    }
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
    onAction: (RestoreAction) -> Unit,
    canResend: Boolean,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = stringResource(R.string.restore_account),
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(PaddingNormal),
                ) {
                    AnimatedVisibility(visible = currentStep == 1) {
                        VerifyEmailContent(
                            isLoading = isLoading,
                            onAction = onAction,
                        )
                    }

                    AnimatedVisibility(visible = currentStep == 2) {
                        VerifyCodeContent(
                            isLoading = isLoading,
                            onAction = onAction,
                            canResend = canResend,
                        )
                    }

                    AnimatedVisibility(visible = currentStep == 3) {
                        ChangePasswordContent(
                            isLoading = isLoading,
                            onAction = onAction,
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
    onAction: (RestoreAction) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.restore_account_description),
        )
        CustomSpacer()
        CustomTextField(
            label = stringResource(R.string.email),
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onAction(RestoreAction.SetEmail(it))
        }
        CustomSpacer()
        CustomButton(text = stringResource(R.string.verify_email), isLoading = isLoading) {
            onAction(RestoreAction.SetAction("email_check"))
        }
    }
}

@Composable
private fun VerifyCodeContent(
    isLoading: Boolean,
    onAction: (RestoreAction) -> Unit,
    canResend: Boolean,
) {
    Column {
        Text(text = stringResource(R.string.enter_the_verification_code_from_your_email))
        CustomSpacer()
        CustomTextField(
            label = stringResource(R.string.code),
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onAction(RestoreAction.SetCode(it))
        }
        CustomSpacer()
        CustomButton(text = stringResource(R.string.verify_code), isLoading = isLoading) {
            onAction(RestoreAction.SetAction("code_check"))
        }
        CustomSpacer()
        AnimatedVisibility(visible = isLoading.not() && canResend) {
            CustomButton(
                text = stringResource(R.string.resend_code),
                buttonType = ButtonType.OUTLINE,
            ) {
                onAction(RestoreAction.SetAction("resend_check"))
            }
        }
    }
}

@Composable
private fun ChangePasswordContent(
    isLoading: Boolean,
    onAction: (RestoreAction) -> Unit,
) {
    Column {
        Text(text = stringResource(R.string.enter_your_new_password))
        CustomSpacer()
        CustomTextField(
            label = stringResource(R.string.new_password),
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onAction(RestoreAction.SetPassword(it))
        }
        CustomSpacer()
        CustomTextField(
            label = stringResource(R.string.confirm_password),
            icon = Icons.Outlined.Email,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onAction(RestoreAction.ConfirmPassword(it))
        }
        CustomSpacer()
        CustomButton(text = stringResource(R.string.confirm), isLoading = isLoading) {
            onAction(RestoreAction.SetAction("password_check"))
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
//            RestoreAccountContent(
//                navController = rememberNavController(),
//                isLoading = false,
//                currentStep = 1,
//                onActionClick = {},
//                onEmailChange = {},
//                onCodeChange = {},
//                onPasswordChange = {},
//                onConfirmPasswordChange = {},
//                canResend = true
//            )
        }
    }
}
