package com.ozyuce.maps.feature.reports.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ChartEntry
import com.ozyuce.maps.feature.reports.domain.model.ChartType
import com.ozyuce.maps.feature.reports.domain.model.ReportType
import com.ozyuce.maps.navigation.HandleUiEvents
import com.ozyuce.maps.ui.theme.OzyuceBlue
import com.ozyuce.maps.ui.theme.OzyuceOrange
import com.ozyuce.maps.ui.theme.SuccessGreen
import com.ozyuce.maps.ui.theme.ErrorRed

/**
 * Raporlama ve analiz ekran?
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: ReportsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle UI events
    HandleUiEvents(
        uiEvents = viewModel.uiEvent,
        navController = navController,
        snackbarHostState = snackbarHostState
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with actions
        HeaderSection(
            selectedReportType = uiState.selectedReportType,
            onReportTypeChanged = viewModel::selectReportType,
            onRefresh = viewModel::refreshData,
            onExportPdf = viewModel::exportToPdf,
            isExporting = uiState.isExporting,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Rapor y?kleniyor...")
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Cards
                uiState.dailyReport?.let { report ->
                    item {
                        SummarySection(report = report)
                    }
                }

                // Charts Section
                item {
                    ChartsSection(
                        attendanceChart = uiState.attendanceChart,
                        performanceChart = uiState.performanceChart,
                        timeAnalysisChart = uiState.timeAnalysisChart
                    )
                }

                // Detailed Report
                uiState.dailyReport?.let { report ->
                    item {
                        DetailedReportSection(report = report)
                    }
                }
            }
        }

        // Error Display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorRed.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = error,
                    color = ErrorRed,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    selectedReportType: ReportType,
    onReportTypeChanged: (ReportType) -> Unit,
    onRefresh: () -> Unit,
    onExportPdf: () -> Unit,
    isExporting: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Raporlar",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Yenile")
                }

                IconButton(
                    onClick = onExportPdf,
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Rounded.Add, contentDescription = "PDF ?ndir")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Report Type Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ReportType.values()) { reportType ->
                FilterChip(
                    selected = selectedReportType == reportType,
                    onClick = { onReportTypeChanged(reportType) },
                    label = {
                        Text(
                            text = when (reportType) {
                                ReportType.DAILY -> "G?nl?k"
                                ReportType.WEEKLY -> "Haftal?k"
                                ReportType.MONTHLY -> "Ayl?k"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    report: DailyReport,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "?zet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                SummaryCard(
                    title = "Toplam Servis",
                    value = "${report.summary.totalServices}",
                    subtitle = "servis turu",
                    color = OzyuceBlue
                )
            }

            item {
                SummaryCard(
                    title = "Kat?l?m Oran?",
                    value = "%.0f%%".format(report.summary.attendanceRate),
                    subtitle = "${report.summary.attendedPersonnel}/${report.summary.totalPersonnel} ki?i",
                    color = SuccessGreen
                )
            }

            item {
                SummaryCard(
                    title = "Toplam S?re",
                    value = "${report.summary.totalDuration}",
                    subtitle = "dakika",
                    color = OzyuceOrange
                )
            }

            item {
                SummaryCard(
                    title = "Mesafe",
                    value = "%.1f km".format(report.summary.totalDistance),
                    subtitle = "toplam yol",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChartsSection(
    attendanceChart: ChartData?,
    performanceChart: ChartData?,
    timeAnalysisChart: ChartData?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Analiz Grafikleri",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Attendance Chart (Pie Chart)
        attendanceChart?.let { chart ->
            ChartCard(
                chartData = chart,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Performance Chart (Bar Chart)
        performanceChart?.let { chart ->
            ChartCard(
                chartData = chart,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Time Analysis Chart (Line Chart)
        timeAnalysisChart?.let { chart ->
            ChartCard(
                chartData = chart
            )
        }
    }
}

@Composable
private fun ChartCard(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = chartData.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (chartData.type) {
                ChartType.PIE_CHART -> PieChartView(chartData.data)
                ChartType.BAR_CHART -> BarChartView(chartData.data)
                ChartType.LINE_CHART -> LineChartView(chartData.data)
                ChartType.DONUT_CHART -> DonutChartView(chartData.data)
            }
        }
    }
}

@Composable
private fun PieChartView(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    // Basit pie chart implementation (Canvas kullan?labilir)
    Column(modifier = modifier) {
        data.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = Color(entry.color ?: 0xFF2196F3),
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    entry.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "%.1f%%".format(entry.value),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BarChartView(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 100f

    Column(modifier = modifier) {
        data.forEach { entry ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "%.0f%%".format(entry.value),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { entry.value / maxValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(entry.color ?: 0xFF2196F3),
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
private fun LineChartView(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    // Basit line chart implementation
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { entry ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "%.0f".format(entry.value),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height((entry.value * 2).dp)
                            .background(
                                color = OzyuceBlue,
                                shape = RoundedCornerShape(1.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = entry.label.take(3),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutChartView(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    // Donut chart i?in PieChart'a benzer implementation
    PieChartView(data, modifier)
}

@Composable
private fun DetailedReportSection(
    report: DailyReport,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Detayl? Rapor",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Rota", report.routeName)
                DetailRow("S?r?c?", report.driverName)
                DetailRow("Tarih", report.date)
                DetailRow("En Erken Ba?lang??", report.timeAnalysis.earliestStart)
                DetailRow("En Ge? Biti?", report.timeAnalysis.latestEnd)
                DetailRow("Ortalama Servis S?resi", "%.1f dakika".format(report.timeAnalysis.averageServiceDuration))
                DetailRow("Dakiklik Skoru", "%.1f%%".format(report.performanceMetrics.punctualityScore))
                DetailRow("Verimlilik Skoru", "%.1f%%".format(report.performanceMetrics.efficiencyScore))
                DetailRow("Genel Skor", "%.1f%%".format(report.performanceMetrics.overallScore))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
