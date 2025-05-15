package com.ih.osm.ui.pages.cilt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.cilt.CiltDetailSection
import com.ih.osm.ui.extensions.defaultScreen

@Composable
fun CiltRoutineScreen(
    navController: NavController,
    userId: String,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        viewModel.loadUserCiltData(userId)
                    }) {
                        Text(stringResource(R.string.download_cilt))
                    }
                }
            }

            if (!state.isLoading && state.userCiltData != null) {
                item {
                    CiltDetailSection(state.userCiltData!!)
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
