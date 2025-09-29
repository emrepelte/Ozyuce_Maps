package com.ozyuce.maps.feature.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.buttons.PrimaryButton

@Composable
fun ServiceCard(
    isActive: Boolean,
    duration: String,
    plateNumber: String,
    routeName: String,
    onToggleService: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = plateNumber,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = routeName,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = duration,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (isActive) "Servisi Bitir" else "Servisi Başlat",
                onClick = onToggleService
            )
        }
    }
}
