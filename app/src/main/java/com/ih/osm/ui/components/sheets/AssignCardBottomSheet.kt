package com.ih.osm.ui.components.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ih.osm.R
import com.ih.osm.domain.model.Employee
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.SectionTag
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.pages.solution.UserCardItem
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.EMPTY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignCardBottomSheet(
    employeeList: List<Employee>,
    showBottomSheet: Boolean,
    onConfirmClick: (Employee) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val list =
        remember {
            mutableStateOf(employeeList)
        }
    val selectedEmployee =
        remember {
            mutableStateOf<Employee?>(null)
        }
    val query =
        remember {
            mutableStateOf(EMPTY)
        }

    Column {
        AnimatedVisibility(visible = showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
            ) {
                LazyColumn(
                    modifier = Modifier.padding(PaddingNormal),
                ) {
                    item {
                        Text(text = "Asignar tarjeta")
                    }
                    item {
                        CustomTextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingNormal),
                            label = stringResource(R.string.search_for_a_user),
                            icon = Icons.Filled.Search,
                        ) {
                            query.value = it
                            list.value =
                                employeeList.filter { item ->
                                    item.name.lowercase().contains(it.lowercase())
                                }
                        }
                    }
                    items(list.value) {
                        CustomSpacer()
                        AnimatedVisibility(visible = query.value.isNotEmpty()) {
                            UserCardItem(
                                title = it.name,
                            ) {
                                selectedEmployee.value = it
                            }
                        }
                    }

                    item {
                        CustomSpacer()
                        AnimatedVisibility(visible = selectedEmployee.value != null) {
                            SectionTag(
                                title = "Assigned User",
                                value = selectedEmployee.value?.name.orEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(visible = selectedEmployee.value != null) {
                            Column {
                                CustomSpacer()

                                CustomButton(text = stringResource(id = R.string.confirm)) {
                                    selectedEmployee.value?.let { onConfirmClick(it) }
                                }
                                CustomSpacer()
                                CustomButton(text = "Descartar", buttonType = ButtonType.TEXT) {
                                    selectedEmployee.value = null
                                    query.value = EMPTY
                                }
                                CustomSpacer()
                            }
                        }
                    }
                }
            }
        }
    }
}
