package com.ih.osm.ui.pages.cilt

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.mockCiltData
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.cilt.CiltDetailSection
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.pages.cilt.action.CiltAction
import com.ih.osm.ui.theme.OsmAppTheme

@Composable
fun CiltScreen(
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingScreen()
    } else if (!state.message.isNullOrEmpty()) {
        Text(
            text = "Error: ${state.message}",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.error,
        )
    } else {
        CiltContent(
            navController = navController,
            data = state.ciltData,
        ) { action ->
            /*
            when (action) {
                CiltAction.GetCilts -> viewModel.handleGetCilts()
            }
             */
        }
    }
}

@Composable
fun CiltContent(
    navController: NavController,
    data: CiltData?,
    onAction: (CiltAction) -> Unit,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                Column {
                    CustomAppBar(
                        navController = navController,
                        title = stringResource(R.string.cilt_routine),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CustomButton(
                            text = stringResource(R.string.download_third_party_cilt),
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.DEFAULT,
                        ) {
                            //
                        }
                        CustomButton(
                            text = stringResource(R.string.non_programmable_cilts),
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.DEFAULT,
                        ) {
                            //
                        }
                    }
                }

                CustomSpacer(space = SpacerSize.SMALL)
            }

            if (data != null) {
                item {
                    CiltDetailSection(
                        data = data,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CiltScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CiltContent(
                navController = rememberNavController(),
                data = mockCiltData(),
            ) {
            }
        }
    }
}
