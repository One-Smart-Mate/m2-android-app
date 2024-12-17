package com.ih.osm.ui.components.card

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ih.osm.R
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.NetworkStatus.*
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.extensions.getColor
import com.ih.osm.ui.extensions.headerContent
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingTinySmall
import com.ih.osm.ui.utils.EMPTY

@Composable
fun NetworkCard(
    networkStatus: NetworkStatus,
    modifier: Modifier = Modifier,
    textColor: Color = getColor(),
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
    ) {
        Text(
            text = getNetworkStatus(networkStatus, context),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                ),
            modifier = Modifier.padding(end = PaddingTinySmall),
        )
        Icon(
            painter = painterResource(id = getNetworkStatusIcon(networkStatus)),
            contentDescription = EMPTY,
            tint = getNetworkStatusColor(networkStatus),
            modifier = Modifier.size(14.dp),
        )
    }
}

private fun getNetworkStatus(
    networkStatus: NetworkStatus,
    context: Context,
): String {
    return when (networkStatus) {
        WIFI_CONNECTED -> context.getString(R.string.wifi_connected)
        WIFI_DISCONNECTED -> context.getString(R.string.wifi_disconnected)
        DATA_CONNECTED -> context.getString(R.string.data_mobile_connected)
        DATA_DISCONNECTED -> context.getString(R.string.data_mobile_disconnected)
        NO_INTERNET_ACCESS -> context.getString(R.string.no_internet_access)
    }
}

private fun getNetworkStatusIcon(networkStatus: NetworkStatus): Int {
    return when (networkStatus) {
        WIFI_CONNECTED -> R.drawable.ic_wifi
        WIFI_DISCONNECTED -> R.drawable.ic_wifi_off
        DATA_CONNECTED -> R.drawable.ic_mobile_data_on
        DATA_DISCONNECTED -> R.drawable.ic_mobile_data_off
        NO_INTERNET_ACCESS -> R.drawable.ic_signal_off
    }
}

@Composable
private fun getNetworkStatusColor(networkStatus: NetworkStatus): Color {
    return when (networkStatus) {
        WIFI_CONNECTED, DATA_CONNECTED -> Color.Green
        WIFI_DISCONNECTED,
        DATA_DISCONNECTED,
        -> Color.Red.copy(alpha = 0.4f)
        NO_INTERNET_ACCESS -> Color.Yellow.copy(alpha = 0.4f)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun NetworkCardPreview() {
    OsmAppTheme {
        Scaffold {
            Column(modifier = Modifier.headerContent(it.calculateTopPadding())) {
                NetworkCard(networkStatus = WIFI_CONNECTED)
                CustomSpacer()
                NetworkCard(networkStatus = WIFI_DISCONNECTED)
                CustomSpacer()
                NetworkCard(networkStatus = DATA_CONNECTED)
                CustomSpacer()
                NetworkCard(networkStatus = DATA_DISCONNECTED)
                CustomSpacer()
                NetworkCard(networkStatus = NO_INTERNET_ACCESS)
            }
        }
    }
}
