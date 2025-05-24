package com.ih.osm.ui.pages.opllist

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.Opl
import com.ih.osm.ui.components.*
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.opl.OplItemCard
import com.ih.osm.ui.components.sheets.FiltersBottomSheet
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.pages.createcard.SectionItemCard
import com.ih.osm.ui.pages.opllist.action.OplListAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.Size120
import com.ih.osm.ui.utils.EMPTY

@Composable
fun OplListScreen(
    navController: NavController,
    viewModel: OplListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        CircularProgressIndicator()
    } else {
        OplListContent(
            navController = navController,
            oplList = state.filteredOplList,
            levelList = state.nodeLevelList,
            selectedLevelList = state.selectedLevelList,
            onAction = { action ->
                when (action) {
                    is OplListAction.Detail -> {
                        // TODO: Navegar a detalle de OPL
                    }
                    is OplListAction.Create -> {
                        // TODO: Navegar a crear OPL
                    }
                    is OplListAction.Filters -> {
                        viewModel.handleAction(action)
                    }
                    is OplListAction.UpdateList -> {
                        viewModel.handleAction(action)
                    }
                    is OplListAction.SetLevel -> {
                        viewModel.handleAction(action)
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OplListContent(
    navController: NavController,
    oplList: List<Opl>,
    levelList: Map<Int, List<NodeCardItem>>,
    selectedLevelList: Map<Int, String>,
    onAction: (OplListAction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(OplListAction.Create)
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = EMPTY)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                Column(
                    modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background,
                            ),
                ) {
                    CustomAppBar(
                        navController = navController,
                        title = "OPL - One Point Lesson",
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            CustomButton(
                                text = stringResource(R.string.update_list),
                                modifier = Modifier.width(Size120),
                                buttonType = ButtonType.TEXT,
                            ) {
                                onAction(OplListAction.UpdateList)
                            }
                            FiltersBottomSheet { filter ->
                                onAction(OplListAction.Filters(filter))
                            }
                        }
                    }
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                LevelContent(
                    levelList = levelList,
                    onLevelClick = { item, key ->
                        onAction(OplListAction.SetLevel(item.id, key))
                    },
                    selectedLevelList = selectedLevelList,
                )
            }

            item {
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            if (oplList.isEmpty()) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingNormal),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No se encontraron OPLs",
                                style =
                                    MaterialTheme.typography.bodyLarge
                                        .copy(
                                            color = getTextColor().copy(alpha = 0.6f),
                                        ),
                            )
                            CustomSpacer()
                            Text(
                                text = "Selecciona un nivel para ver los OPLs disponibles",
                                style =
                                    MaterialTheme.typography.bodyMedium
                                        .copy(
                                            color = getTextColor().copy(alpha = 0.4f),
                                        ),
                            )
                        }
                    }
                }
            } else {
                items(oplList) { opl ->
                    OplItemCard(
                        opl = opl,
                        onClick = {
                            onAction(OplListAction.Detail(opl.id))
                        },
                    )
                }

                item {
                    CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                    CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                }
            }
        }
    }
}

@Composable
fun LevelContent(
    levelList: Map<Int, List<NodeCardItem>>,
    onLevelClick: (NodeCardItem, key: Int) -> Unit,
    selectedLevelList: Map<Int, String>,
) {
    AnimatedVisibility(visible = levelList.isNotEmpty()) {
        Column {
            CustomSpacer()

            levelList.forEach { level ->
                if (level.value.isNotEmpty()) {
                    Text(
                        text = "${stringResource(R.string.level)} ${level.key}",
                        style =
                            MaterialTheme.typography.titleLarge
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                    LazyRow {
                        items(level.value) { item ->
                            SectionItemCard(
                                title = item.name,
                                description = item.description,
                                selected = item.id == selectedLevelList[level.key],
                            ) {
                                onLevelClick(item, level.key)
                            }
                        }
                    }
                    CustomSpacer()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun OplListScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            OplListContent(
                navController = rememberNavController(),
                oplList = Opl.mockOplList(),
                levelList = emptyMap(),
                selectedLevelList = emptyMap(),
                onAction = {},
            )
        }
    }
}
