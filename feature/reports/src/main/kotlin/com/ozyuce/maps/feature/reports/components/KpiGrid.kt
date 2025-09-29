package com.ozyuce.maps.feature.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.cards.KpiCard
import com.ozyuce.maps.core.ui.components.cards.PersonnelKpiCard
import com.ozyuce.maps.core.ui.components.cards.TimeDistanceKpiCard
import com.ozyuce.maps.feature.reports.KpiData

@Composable
fun KpiGrid(
    data: KpiData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PersonnelKpiCard(
                title = "Toplam Personel",
                value = data.totalPersonnel.toString(),
                modifier = Modifier.weight(1f)
            )
            PersonnelKpiCard(
                title = "Binen",
                value = data.onBoard.toString(),
                modifier = Modifier.weight(1f)
            )
            PersonnelKpiCard(
                title = "Binmeyen",
                value = data.absent.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeDistanceKpiCard(
                title = "Toplam Süre",
                value = data.duration,
                modifier = Modifier.weight(1f)
            )
            TimeDistanceKpiCard(
                title = "Toplam Mesafe",
                value = data.distance,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
