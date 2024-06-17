package com.ih.m2.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ih.m2.R
import com.ih.m2.ui.components.ButtonType
import com.ih.m2.ui.components.CircularImage
import com.ih.m2.ui.components.CustomButton
import com.ih.m2.ui.components.CustomIconButton
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTag
import com.ih.m2.ui.components.RadioGroup
import com.ih.m2.ui.components.SectionTag
import com.ih.m2.ui.components.SpacerDirection
import com.ih.m2.ui.components.TagSize
import com.ih.m2.ui.components.TagType
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.headerContent
import com.ih.m2.ui.navigation.navigateToAccount
import com.ih.m2.ui.navigation.navigateToCardDetail
import com.ih.m2.ui.navigation.navigateToCreateCard
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigateToCreateCard()
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.empty))
            }
        }
    ) { _ ->
        HomeScreenContent(navController = navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    navController: NavHostController
) {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    LazyColumn {
        stickyHeader {
            HomeScreenTitle(navController = navController, onFilterClick = {
                showBottomSheet = true
            })
        }

        items(5){
            CardItemList()
        }


        item {
            //  if (true) {
//                ModalBottomSheet(onDismissRequest = {
//
//                }) {
//                    FiltersBottomSheetDialog
//                }

            //  }
        }
    }
}

@Composable
fun HomeScreenTitle(navController: NavHostController, onFilterClick: () -> Unit) {
    Column(
        modifier = Modifier.headerContent(),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = stringResource(R.string.empty),
            tint = getColor(),
            modifier = Modifier.clickable {
                navController.navigateToAccount()
            })
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularImage(image = "https://www.gravatar.com/avatar/2c7d99fe281ecd3bcd65ab915bac6dd5?s=250")
            CustomSpacer(direction = SpacerDirection.HORIZONTAL)
            Column {
                Text(
                    text = "Welcome back Fausto Camano",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = getColor()
                    )
                )
                Text(
                    text = "Company  Universal S.A de CV",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = getColor()
                    )
                )
                Row {
                    CustomTag(
                        title = "Admin",
                        tagSize = TagSize.SMALL,
                        tagType = TagType.OUTLINE,
                        invertedColors = true,
                    )
                }
            }
        }
        CustomSpacer()
        CustomIconButton(text = stringResource(R.string.filters), icon = Icons.Default.Menu) {
            onFilterClick()
        }
        CustomSpacer()
    }
}

@Composable
fun CardItemList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingNormal)
    ) {
        Column(
            modifier = Modifier.padding(PaddingNormal)
        ) {
            Text(
                text = "Anomialias 1", style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            SectionTag("Estatus", "Provisional Solution")
            SectionTag("Type", "Anomalias")
            SectionTag("Classifier", "B")
            SectionTag("Area", "Envasado")
            SectionTag("Level", "Taponadora 2")
            SectionTag("Creator", "Fausto Camanio")
            SectionTag("Date", "24 Marzo, 2024")
            SectionTag("Due Date", "27 Marzo, 2024")
            CustomSpacer()
            CustomButton(text = "Actions", buttonType = ButtonType.OUTLINE) {

            }
        }
    }
}


@Composable
fun FiltersBottomSheetDialog(
    selection: String,
    onFilterChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier.padding(PaddingNormal),
    ) {
        Text(
            text = stringResource(R.string.filters), style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)

        )
        RadioGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                stringResource(R.string.all_open_cards),
                stringResource(R.string.my_open_cards),
                stringResource(R.string.assigned_cards),
                stringResource(R.string.unassigned_cards),
                stringResource(R.string.expired_cards),
                stringResource(R.string.closed_cards)
            ),
            selection = selection,
        ) {
            onFilterChange(it)
        }
        CustomSpacer()
        CustomButton(text = stringResource(R.string.apply)) {
            onApply()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun HomePreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeScreen(rememberNavController())
        }
    }
}