package com.ozyuce.maps.feature.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.KpiCard
import com.ozyuce.maps.feature.reports.KpiData

@Composable
fun KpiGrid(
    data: KpiData,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        KpiGridItem(
            icon = Icons.Rounded.Person,
            title = "Toplam Personel",
            value = data.totalPersonnel.toString(),
            trend = data.totalPersonnelTrend
        ),
        KpiGridItem(
            icon = Icons.AutoMirrored.Rounded.DirectionsWalk,
            title = "Binen",
            value = data.onBoard.toString(),
            trend = data.onBoardTrend
        ),
        KpiGridItem(
            icon = Icons.Rounded.PersonOff,
            title = "Binmeyen",
            value = data.absent.toString(),
            trend = data.absentTrend
        ),
        KpiGridItem(
            icon = Icons.Rounded.DirectionsBus,
            title = "Geciken Personel",
            value = data.late.toString(),
            trend = data.lateTrend
        ),
        KpiGridItem(
            icon = Icons.Rounded.AccessTime,
            title = "Toplam Süre",
            value = data.duration,
            trend = data.durationTrend
        ),
        KpiGridItem(
            icon = Icons.Rounded.Straighten,
            title = "Toplam Mesafe",
            value = data.distance,
            trend = data.distanceTrend
        )
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            KpiCard(
                icon = item.icon,
                title = item.title,
                value = item.value,
                trendPercent = item.trend,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class KpiGridItem(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val trend: Float?
)
