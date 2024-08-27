package com.osm.ui.components.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.osm.R
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.RadioGroup
import com.osm.ui.components.SpacerSize
import com.osm.ui.components.buttons.ButtonType
import com.osm.ui.components.buttons.CustomButton
import com.osm.ui.components.buttons.CustomIconButton
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.utils.EMPTY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    selection: String,
    onFilterChange: (String) -> Unit,
    onApply: () -> Unit,
    onDismissRequest: () -> Unit,
    onCleanFilers: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        FiltersBottomSheetContent(selection, onFilterChange, onApply, onCleanFilers)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheetV2(
    onFilterChange: (String) -> Unit,
    onClickApply: () -> Unit,
) {

    var showFiltersBottomSheet by remember {
        mutableStateOf(false)
    }

    CustomIconButton(text = stringResource(R.string.filters), icon = Icons.Outlined.Menu) {
        showFiltersBottomSheet = true
    }

    AnimatedVisibility(visible = showFiltersBottomSheet) {
        ModalBottomSheet(onDismissRequest = {
            showFiltersBottomSheet = false
        }) {
            FiltersBottomSheetContentV2(
                onFilterChange
            ) {
                showFiltersBottomSheet = false
                onClickApply()
            }
        }
    }
}

@Composable
fun FiltersBottomSheetContentV2(
    onFilterChange: (String) -> Unit,
    onClickApply: () -> Unit,
) {
    var selection by remember {
        mutableStateOf(EMPTY)
    }
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
            selection = it
            onFilterChange(it)
        }
        CustomSpacer()
        CustomButton(text = stringResource(R.string.apply)) {
            onClickApply()
        }
        CustomSpacer()
        CustomButton(
            text = stringResource(R.string.clean_filters),
            buttonType = ButtonType.OUTLINE
        ) {
            onFilterChange(EMPTY)
        }
        CustomSpacer(space = SpacerSize.LARGE)
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
        CustomButton(
            text = stringResource(R.string.clean_filters),
            buttonType = ButtonType.OUTLINE
        ) {
            onCleanFilers()
        }
        CustomSpacer(space = SpacerSize.LARGE)
    }
}