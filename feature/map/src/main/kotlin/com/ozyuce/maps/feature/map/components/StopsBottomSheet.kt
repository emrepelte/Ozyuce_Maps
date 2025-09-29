package com.ozyuce.maps.feature.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.map.StopStatus
import com.ozyuce.maps.feature.map.StopUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsBottomSheet(
    stops: List<StopUiState>,
    onStopClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.testTag("map_bottom_sheet")) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.small
                    )
                    .testTag("map_bottom_sheet_handle")
            )
        }

        Text(
            text = "Sıradaki Duraklar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        stops.forEach { stop ->
            StopItem(
                stop = stop,
                onClick = { onStopClick(stop.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopItem(
    stop: StopUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = stop.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "Tahmini varış: ${stop.eta}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            val color = when (stop.status) {
                StopStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                StopStatus.NEXT -> MaterialTheme.colorScheme.tertiary
                StopStatus.PENDING -> MaterialTheme.colorScheme.outline
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = color, shape = MaterialTheme.shapes.small)
            )
        },
        trailingContent = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Rounded.NavigateNext,
                    contentDescription = "Durağa git"
                )
            }
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    )
}
