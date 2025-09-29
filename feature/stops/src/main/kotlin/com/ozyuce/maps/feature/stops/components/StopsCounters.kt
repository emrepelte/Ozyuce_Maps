package com.ozyuce.maps.feature.stops.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.cards.KpiCard

@Composable
fun StopsCounters(
    totalCount: Int,
    onBoardCount: Int,
    remainingCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Toplam",
            value = totalCount.toString(),
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Binen",
            value = onBoardCount.toString(),
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Kalan",
            value = remainingCount.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}
