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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.ui.components.CustomButton
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTextField
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.navigation.Screen
import com.ih.m2.ui.theme.M2androidappTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = mavericksViewModel(),
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            LoginTitle()
            Spacer(modifier = Modifier.fillMaxHeight())
            LoginContent(viewModel, navController, modifier = Modifier.fillParentMaxSize())
        }
    }
}

@Composable
fun LoginContent(viewModel: LoginViewModel, navController: NavController, modifier: Modifier) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Card(
        shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomTextField(
                value = "",
                modifier = Modifier.fillMaxWidth(),
                label = "Email",
                placeholder = "Enter your email",
                icon = Icons.Default.Email
            ) {

            }
            CustomSpacer()
            CustomTextField(
                value = "",
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                placeholder = "Enter your password",
                icon = Icons.Default.Person
            ) {

            }
            CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            CustomButton(text = "Login", isLoading = state.isLoading) {
                viewModel.process(
                    LoginViewModel.Action.Login(
                        "fausto52@hotmail.com",
                        "12345678"
                    )
                )
            }
            if (state.errorMessage.isNotEmpty()) {
                Toast.makeText(LocalContext.current, state.errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
            LaunchedEffect(viewModel, lifecycle) {
                snapshotFlow { state }
                    .flowWithLifecycle(lifecycle)
                    .collect {
                        if (it.isAuthenticated) {
                            navController.navigate(Screen.Home.route)
                        }
                    }
            }
        }
    }
}

@Composable
fun LoginTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center,

        ) {
        Text(
            text = "M2 App", style = MaterialTheme.typography.displayMedium
                .copy(color = Color.White)
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
            LoginScreen(navController = rememberNavController())
        }
    }
}