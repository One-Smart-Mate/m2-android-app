package com.ih.m2.ui.components.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ih.m2.R
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.RadioGroup
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.buttons.ButtonType
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.theme.PaddingNormal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    selection: String,
    onFilterChange: (String) -> Unit,
    onApply: () -> Unit,
    onDismissRequest:() -> Unit,
    onCleanFilers: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        FiltersBottomSheetContent(selection, onFilterChange, onApply, onCleanFilers)
    }
}

@Composable
fun FiltersBottomSheetContent(
    selection: String,
    onFilterChange: (String) -> Unit,
    onApply: () -> Unit,
    onCleanFilers: () -> Unit
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
        CustomSpacer()
        CustomButton(text = stringResource(R.string.clean_filters), buttonType = ButtonType.OUTLINE) {
            onCleanFilers()
        }
        CustomSpacer(space = SpacerSize.LARGE)
    }
}