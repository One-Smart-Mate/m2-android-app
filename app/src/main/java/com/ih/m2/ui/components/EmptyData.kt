package com.ih.m2.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.ui.extensions.getPrimaryColor
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingToolbar
import com.ih.m2.ui.theme.Size100
import com.ih.m2.ui.theme.Size120
import com.ih.m2.ui.theme.Size150
import com.ih.m2.ui.utils.EMPTY


@Composable
fun EmptyData(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = Color.Gray,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "color"
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cloud),
            contentDescription = EMPTY,
            tint = getPrimaryColor(),
            modifier = Modifier.size(Size100)
        )
        Text(text = stringResource(R.string.empty_data), style = MaterialTheme.typography.titleLarge
            .copy(color = animatedColor))

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EmptyDataPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Column {
                EmptyData()
            }
        }
    }
}