package com.ozyuce.maps.feature.reports.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.KpiCard
import com.ozyuce.maps.feature.reports.ReportsUiState

@Composable
fun ReportPrintLayout(uiState: ReportsUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Rapor Özeti", style = MaterialTheme.typography.titleMedium)

        // KPI'ların non-lazy düzeni (sabit yükseklikli kartlar alt alta)
        KpiCard(Icons.Rounded.Badge, "Toplam Personel", uiState.totalStaff.toString())
        KpiCard(Icons.Rounded.DirectionsWalk, "Binen", uiState.boardedCount.toString())
        KpiCard(Icons.Rounded.RemoveCircleOutline, "Binmeyen", uiState.absentCount.toString())
        KpiCard(Icons.Rounded.Schedule, "Toplam Süre", uiState.totalDurationText)
        KpiCard(Icons.Rounded.Straighten, "Toplam Mesafe", uiState.totalDistanceText)
        KpiCard(Icons.Rounded.WarningAmber, "Geciken Personel", uiState.lateCount.toString())

        Spacer(Modifier.height(16.dp))

        // İleride: Grafikler için de non-lazy/tek parça çizim eklenebilir
    }
}

