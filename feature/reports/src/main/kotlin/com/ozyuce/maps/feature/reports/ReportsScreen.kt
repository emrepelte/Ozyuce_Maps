package com.ozyuce.maps.feature.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.ui.components.KpiCard
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.events.UiEvent
import com.ozyuce.maps.core.ui.export.exportViewAsPdf
import com.ozyuce.maps.core.ui.export.exportViewAsPng
import com.ozyuce.maps.core.ui.export.renderComposableToView
import com.ozyuce.maps.core.ui.export.shareFile
import com.ozyuce.maps.feature.reports.components.DateFilter
import com.ozyuce.maps.feature.reports.components.charts.BarChart
import com.ozyuce.maps.feature.reports.components.charts.DonutChart
import com.ozyuce.maps.feature.reports.ui.ReportPrintLayout
import com.ozyuce.maps.feature.reports.ui.components.FiltersSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onProfileClick: () -> Unit = {},
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Raporlar",
                onProfileClick = onProfileClick,
                actions = {
                    IconButton(onClick = { showShareSheet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Raporu paylaş"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isLoading = true
                    viewModel.onEvent(ReportsEvent.RefreshData)
                    scope.launch {
                        delay(800)
                        isLoading = false
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Yenile"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    DateFilter(
                        selectedType = uiState.selectedFilterType,
                        selectedRange = uiState.selectedDateRange,
                        onFilterTypeChanged = { viewModel.onEvent(ReportsEvent.FilterTypeChanged(it)) },
                        onDateRangeChanged = { viewModel.onEvent(ReportsEvent.DateRangeChanged(it)) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("reports_filter")
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        FiltersSection(
                            plates = uiState.plates,
                            drivers = uiState.drivers,
                            vehicleTypes = uiState.vehicleTypes,
                            selectedPlate = uiState.filterPlate,
                            onPlate = { viewModel.onEvent(ReportsEvent.FilterPlate(it)) },
                            selectedDriver = uiState.filterDriver,
                            onDriver = { viewModel.onEvent(ReportsEvent.FilterDriver(it)) },
                            selectedVehicle = uiState.filterVehicle,
                            onVehicle = { viewModel.onEvent(ReportsEvent.FilterVehicle(it)) },
                        )
                    }
                }

                // KPI Kartları için grid yapısı
                val kpiCards = listOf(
                    KpiCardData(Icons.Rounded.Badge, "Toplam Personel", uiState.totalStaff.toString()),
                    KpiCardData(Icons.Rounded.DirectionsWalk, "Binen", uiState.boardedCount.toString()),
                    KpiCardData(Icons.Rounded.RemoveCircleOutline, "Binmeyen", uiState.absentCount.toString()),
                    KpiCardData(Icons.Rounded.Schedule, "Toplam Süre", uiState.totalDurationText),
                    KpiCardData(Icons.Rounded.Straighten, "Toplam Mesafe", uiState.totalDistanceText),
                    KpiCardData(Icons.Rounded.WarningAmber, "Geciken Personel", uiState.lateCount.toString())
                )

                items(kpiCards.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { card ->
                            Box(modifier = Modifier.weight(1f)) {
                                KpiCard(
                                    icon = card.icon,
                                    title = card.title,
                                    value = card.value
                                )
                            }
                        }
                    }
                }

                item {
                    ReportSection(
                        title = "Katılım Oranı",
                        content = {
                            DonutChart(
                                data = uiState.attendanceData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .testTag("reports_donut_chart")
                            )
                        }
                    )
                }

                item {
                    ReportSection(
                        title = "Saatlik Dağılım",
                        content = {
                            BarChart(
                                data = uiState.hourlyData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .testTag("reports_bar_chart")
                            )
                        }
                    )
                }
            }

            // Yükleme göstergesi
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Raporlar Yükleniyor",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Paylaşım alt sayfası
    if (showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
            sheetState = sheetState
        ) {
            val pageWidthPx = 1240
            val pageHeightPx = 1754
            val buildReportView = {
                renderComposableToView(
                    context = context,
                    widthPx = pageWidthPx,
                    heightPx = pageHeightPx
                ) { ReportPrintLayout(uiState) }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Raporu Dışa Aktar / Paylaş", style = MaterialTheme.typography.titleMedium)

                Button(onClick = {
                    val viewToExport = buildReportView()
                    val file = exportViewAsPng(viewToExport, "rapor_${System.currentTimeMillis()}")
                    shareFile(context, file, "image/png", subject = "Rapor - PNG")
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showShareSheet = false
                    }
                }) { Text("PNG olarak paylaş") }

                Button(onClick = {
                    val viewToExport = buildReportView()
                    val file = exportViewAsPdf(viewToExport, "rapor_${System.currentTimeMillis()}")
                    shareFile(context, file, "application/pdf", subject = "Rapor - PDF")
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showShareSheet = false
                    }
                }) { Text("PDF olarak paylaş") }

                Button(onClick = {
                    val viewToExport = buildReportView()
                    val file = exportViewAsPdf(viewToExport, "rapor_${System.currentTimeMillis()}")
                    shareFile(
                        context,
                        file,
                        "application/pdf",
                        subject = "Rapor",
                        body = "İlgili raporu ekte bulabilirsiniz."
                    )
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showShareSheet = false
                    }
                }) { Text("E-posta ile gönder (PDF)") }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReportSection(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

private data class KpiCardData(
    val icon: ImageVector,
    val title: String,
    val value: String
)
