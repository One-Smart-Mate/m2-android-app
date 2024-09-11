package com.ih.osm.ui.components.sheets

import androidx.compose.animation.AnimatedVisibility
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
import com.ih.osm.R
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionBottomSheet(
    onSolutionClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    showProvisionalSolution: Boolean,
    showDefinitiveSolution: Boolean
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        SolutionBottomSheetContent(showProvisionalSolution, showDefinitiveSolution) {
            onSolutionClick(it)
        }
    }
}

@Composable
fun SolutionBottomSheetContent(
    showProvisionalSolution: Boolean,
    showDefinitiveSolution: Boolean,
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
        AnimatedVisibility(visible = showProvisionalSolution) {
            CustomButton(text = stringResource(R.string.provisional_solution)) {
                onSolutionClick(PROVISIONAL_SOLUTION)
            }
        }
        CustomSpacer()
        AnimatedVisibility(visible = showDefinitiveSolution) {
            CustomButton(
                text = stringResource(R.string.definitive_solution),
                buttonType = ButtonType.OUTLINE
            ) {
                onSolutionClick(DEFINITIVE_SOLUTION)
            }
        }
    }
}

@Composable
@Preview
fun SolutionBottomSheetPreview() {
    OsmAppTheme {
        Surface {
            SolutionBottomSheetContent(true, true) {

            }
        }
    }
}