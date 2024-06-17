package com.ih.m2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ih.m2.R
import com.ih.m2.ui.extensions.getTextColor
import com.ih.m2.ui.theme.PaddingLarge
import com.ih.m2.ui.theme.Size38

@Composable
fun CustomAppBar(navController: NavController, title: String) {
    Column {
        Icon(
            Icons.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.empty),
            modifier = Modifier
                .size(Size38)
                .clickable {
                    navController.popBackStack()
                },
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall
                .copy(color = getTextColor()),
            modifier = Modifier.padding(horizontal = PaddingLarge)
        )
        CustomSpacer()
    }
}