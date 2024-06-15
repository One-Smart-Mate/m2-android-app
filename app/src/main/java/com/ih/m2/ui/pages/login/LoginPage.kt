package com.ih.m2.ui.pages.login

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ih.m2.ui.components.CustomButton
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTextField
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.theme.M2androidappTheme
@Composable
fun LoginPage() {
    val viewModel: LoginViewModel = hiltViewModel()
    LazyColumn(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center,

                ) {
                Text(text = "M2 App", style = MaterialTheme.typography.displayMedium
                    .copy(color = Color.White))
            }
            Spacer(modifier = Modifier.fillMaxHeight())
            Card(
                shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
                modifier = Modifier.fillParentMaxSize()
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
                    CustomButton(text = "Login") {

                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun LoginPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            LoginPage()
        }
    }
}