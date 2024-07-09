package com.ih.m2.ui.pages.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.m2.R
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getPrimaryColor
import com.ih.m2.ui.navigation.navigateToHome
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.Size40
import com.ih.m2.ui.utils.CARD_ANOMALIES
import com.ih.m2.ui.utils.CARD_BEHAVIOR
import com.ih.m2.ui.utils.EMPTY

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreHomeScreen(
    navController: NavController
) {
    Scaffold {
        LazyColumn(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
                .defaultScreen(it)
        ) {
            stickyHeader {
                CustomSpacer(space = SpacerSize.LARGE)
                Text(
                    text = stringResource(R.string.select_a_card_type),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = getColor()
                    )
                )
                CustomSpacer()
            }
            item {
                Column {

                    PreHomeCardItem(
                        title = stringResource(R.string.anomalies),
                        painter = painterResource(id = R.drawable.ic_settings_square)
                    ) {
                        navController.navigateToHome(CARD_ANOMALIES)
                    }
                    PreHomeCardItem(
                        title = stringResource(R.string.behavior),
                        painter = painterResource(id = R.drawable.ic_supervised)
                    ) {
                        navController.navigateToHome(CARD_BEHAVIOR)
                    }
                }
            }
        }
    }
}

@Composable
fun PreHomeCardItem(
    title: String,
    painter: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingNormal),
        colors = CardDefaults.cardColors(
            contentColor = getPrimaryColor()
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = EMPTY,
                modifier = Modifier.size(Size40)
            )
            Text(
                text = title,
                modifier = Modifier.padding(PaddingNormal),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun PreviewPreHomeScreen() {
    M2androidappTheme {
        PreHomeScreen(rememberNavController())
    }
}