package com.ih.m2.ui.pages.createcard

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getPrimaryColor
import com.ih.m2.ui.extensions.getTextColor
import com.ih.m2.ui.pages.account.AccountContent
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.PaddingTiny
import com.ih.m2.ui.theme.PaddingToolbar
import com.ih.m2.ui.theme.Size150

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateCardScreen(
    navController: NavController
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = PaddingToolbar, horizontal = PaddingTiny)
    ) {
        stickyHeader {
            CustomAppBar(navController = navController, title = "Create card")
        }
        item {

            SectionCard(
                "Priority",
                listOf("1","2"),
            ) {

            }

        }
    }
}

@Composable
fun SectionCard(
    section: String,
    list: List<String>,
    onItemClick: (String) -> Unit
) {
    Text(
        text = section, style = MaterialTheme.typography.titleLarge
            .copy(fontWeight = FontWeight.Bold)
    )

    LazyRow {
        items(list) {
            ItemCard(title = "", description = "") {
                onItemClick("")
            }
        }
    }
}

@Composable
fun ItemCard(
    title: String,
    description: String,
    selected: Boolean = false,
    onItemClick: () -> Unit
) {
    val color = if (selected) {
        CardDefaults.cardColors(
            contentColor = getColor(),
            containerColor = getPrimaryColor()
        )
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = Modifier
            .padding(PaddingTiny)
            .width(Size150),
        colors = color,
        onClick = {
            onItemClick()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingNormal)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
                    .copy(fontWeight = FontWeight.W700)
            )
            Text(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun CreateCardPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CreateCardScreen(
                navController = rememberNavController(),
            )
        }
    }
}