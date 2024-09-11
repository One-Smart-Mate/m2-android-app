package com.ih.osm.ui.pages.profile

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.R
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.User
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.images.CircularImage
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.pages.error.ErrorScreen
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.Size120

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = mavericksViewModel()
) {

    val state by viewModel.collectAsState()
    when(val result = state.state) {
        is LCE.Loading, LCE.Uninitialized -> {
            LoadingScreen(text = stringResource(id = R.string.loading_data))
        }
        is LCE.Success -> {
            ProfileContent(
                navController = navController,
                user = result.value
            )
        }
        is LCE.Fail -> {
            ErrorScreen(navController = navController, errorMessage = result.error) {
                viewModel.process(ProfileViewModel.Action.GetUser)
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileContent(
    navController: NavController,
    user: User
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = stringResource(R.string.profile)
                )
            }

            item {

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularImage(image = user.logo, size = Size120)
                }

                CustomSpacer()

                ListItem(
                    headlineContent = { Text("Full name") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    trailingContent = {
                        Text(text = user.name)
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.company)) },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account_balance),
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    trailingContent = {
                        Text(text = user.companyName)
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.site)) },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_business_center),
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    trailingContent = {
                        Text(text = user.siteName)
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.email)) },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = stringResource(R.string.empty),
                        )
                    },
                    tonalElevation = PaddingNormal,
                    trailingContent = {
                        Text(text = user.email)
                    }
                )
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun ProfilePreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ProfileContent(rememberNavController(), User.mockUser())
        }
    }
}