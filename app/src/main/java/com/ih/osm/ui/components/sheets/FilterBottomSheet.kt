package com.ih.osm.ui.components.sheets

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
import com.ih.osm.R
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.RadioGroup
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.buttons.CustomIconButton
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.EMPTY


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    onFilterChange: (String) -> Unit,
) {
    var selection by remember {
        mutableStateOf(EMPTY)
    }
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
            FiltersBottomSheetContent(selection = selection) {
                onFilterChange(it)
                showFiltersBottomSheet = false
                selection = it
            }
        }
    }
}

@Composable
fun FiltersBottomSheetContent(
    selection: String,
    onFilterChange: (String) -> Unit,
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
        CustomButton(
            text = stringResource(R.string.clean_filters),
            buttonType = ButtonType.OUTLINE
        ) {
            onFilterChange(EMPTY)
        }
        CustomSpacer(space = SpacerSize.LARGE)
    }
}