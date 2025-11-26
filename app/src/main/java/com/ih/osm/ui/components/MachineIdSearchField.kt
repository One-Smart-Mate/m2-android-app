package com.ih.osm.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ih.osm.R

@Composable
fun MachineIdSearchField(
    searchQuery: String,
    isSearching: Boolean,
    errorMessage: String,
    successMessage: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Title
        Text(
            text = stringResource(R.string.search_by_machine_id),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Search Row: Input + Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_machine_id),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                isError = errorMessage.isNotEmpty(),
                enabled = !isSearching,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions =
                    KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            onSearch()
                        },
                    ),
                modifier =
                    Modifier
                        .weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    onSearch()
                },
                enabled = !isSearching && searchQuery.isNotBlank(),
                modifier = Modifier.height(56.dp),
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(20.dp).height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                    )
                }
            }
        }

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
            )
        }

        // Processing message
        if (isSearching) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.processing_levels),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
            )
        }

        // Success message
        if (successMessage && !isSearching) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.machine_id_found),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
            )
        }

        // Help text (only show when not searching and no error/success)
        if (!isSearching && errorMessage.isEmpty() && !successMessage) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You can skip this and select levels manually below",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
            )
        }
    }
}
