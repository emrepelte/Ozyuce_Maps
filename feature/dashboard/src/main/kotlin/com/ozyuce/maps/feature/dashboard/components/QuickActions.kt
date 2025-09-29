package com.ozyuce.maps.feature.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun QuickActions(
    onScanStop: () -> Unit,
    onPassengerList: () -> Unit,
    onReports: () -> Unit,
    onPortal: () -> Unit,
    onArvento: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "H?zl? Eylemler",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = Icons.Rounded.QrCodeScanner,
                text = "Durak Tara",
                onClick = onScanStop,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Rounded.People,
                text = "Yolcu Listesi",
                onClick = onPassengerList,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Rounded.Assessment,
                text = "Raporlar",
                onClick = onReports,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kestirmeler",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = Icons.Rounded.Web,
                text = "Portal",
                onClick = onPortal,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Rounded.LocationOn,
                text = "Arvento",
                onClick = onArvento,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
