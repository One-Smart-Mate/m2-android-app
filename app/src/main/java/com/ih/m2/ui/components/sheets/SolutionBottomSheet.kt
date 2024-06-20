package com.ih.m2.ui.components.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.buttons.ButtonType
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.utils.DEFINITIVE_SOLUTION
import com.ih.m2.ui.utils.PROVISIONAL_SOLUTION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionBottomSheet(
    onSolutionClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        SolutionBottomSheetContent {
            onSolutionClick(it)
        }
    }
}

@Composable
fun SolutionBottomSheetContent(
    onSolutionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(PaddingNormal)
    ) {
        Text(
            text = stringResource(R.string.actions), style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        CustomSpacer(space = SpacerSize.EXTRA_LARGE)
        CustomButton(text = stringResource(R.string.provisional_solution)) {
            onSolutionClick(PROVISIONAL_SOLUTION)
        }
        CustomSpacer()
        CustomButton(
            text = stringResource(R.string.definitive_solution),
            buttonType = ButtonType.OUTLINE
        ) {
            onSolutionClick(DEFINITIVE_SOLUTION)
        }
    }
}

@Composable
@Preview
fun SolutionBottomSheetPreview() {
    M2androidappTheme {
        Surface {
            SolutionBottomSheetContent {

            }
        }
    }
}