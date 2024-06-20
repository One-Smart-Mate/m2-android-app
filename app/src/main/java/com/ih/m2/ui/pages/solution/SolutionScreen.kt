package com.ih.m2.ui.pages.solution

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.R
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTextField
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getInvertedColor
import com.ih.m2.ui.extensions.getTextColor
import com.ih.m2.ui.pages.carddetail.CardDetailViewModel
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingLarge
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.PaddingTiny
import com.ih.m2.ui.utils.DEFINITIVE_SOLUTION
import com.ih.m2.ui.utils.PROVISIONAL_SOLUTION


@Composable
fun SolutionScreen(
    navController: NavController,
    solutionType: String,
    cardId: String,
    viewModel: SolutionViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()
    SolutionScreenContent(
        navController = navController,
        title = getSolutionScreenTitle(state.solutionType)
    )

    LaunchedEffect(viewModel) {
        viewModel.process(SolutionViewModel.Action.SetSolutionInfo(solutionType, cardId))
    }
}

@Composable
fun getSolutionScreenTitle(solutionType: String): String {
    return when (solutionType) {
        DEFINITIVE_SOLUTION -> {
            stringResource(R.string.definitive_solution)
        }

        PROVISIONAL_SOLUTION -> {
            stringResource(R.string.provisional_solution)
        }

        else -> stringResource(R.string.empty)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SolutionScreenContent(
    title: String,
    navController: NavController
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = title
                )
            }
            item {
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingNormal),
                    label = "Operator",
                    value = "",
                    icon = Icons.Filled.Person
                ) {

                }
            }
            items(4) {
                UserCardItem()
            }

            item {
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingNormal),
                    label = "Solution",
                    value = "",
                    icon = Icons.Filled.Create
                ) {

                }
                CustomSpacer()
                CustomButton(text = "Save") {
                    
                }
            }
        }
    }
}

@Composable
fun UserCardItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingLarge, vertical = 1.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = "User nameee",
            style = MaterialTheme.typography.bodyLarge
                .copy(color = getColor()),
            modifier = Modifier.padding(PaddingNormal)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun SolutionScreenPreview() {
    M2androidappTheme {
        Surface {
            SolutionScreenContent("Provisional solution", rememberNavController())
        }
    }
}