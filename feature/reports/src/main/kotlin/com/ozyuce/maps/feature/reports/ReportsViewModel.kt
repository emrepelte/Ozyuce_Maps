package com.ozyuce.maps.feature.reports

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        loadMockData()
    }

    fun onEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.DateRangeChanged -> updateDateRange(event.range)
            is ReportsEvent.FilterTypeChanged -> updateFilterType(event.type)
            is ReportsEvent.ExportReport -> exportReport(event.context, event.bitmap)
            is ReportsEvent.FilterPlate -> updateFilterPlate(event.plate)
            is ReportsEvent.FilterDriver -> updateFilterDriver(event.driver)
            is ReportsEvent.FilterVehicle -> updateFilterVehicle(event.vehicle)
            is ReportsEvent.ToggleCustomer -> toggleCustomer(event.customer)
            ReportsEvent.ClearCustomers -> clearCustomers()
            is ReportsEvent.RefreshData -> loadMockData()
            ReportsEvent.ResetFilters -> resetFilters()
        }
    }
    
    private fun toggleCustomer(customer: String) {
        val currentSelection = _uiState.value.selectedCustomers
        _uiState.value = _uiState.value.copy(
            selectedCustomers = if (currentSelection.contains(customer)) {
                currentSelection.filter { it != customer }
            } else {
                currentSelection + customer
            }
        )
        loadMockData()
    }
    
    private fun clearCustomers() {
        _uiState.value = _uiState.value.copy(selectedCustomers = emptyList())
        loadMockData()
    }

    private fun updateDateRange(range: DateRange) {
        _uiState.value = _uiState.value.copy(selectedDateRange = range)
        loadMockData()
    }

    private fun updateFilterType(type: FilterType) {
        _uiState.value = _uiState.value.copy(selectedFilterType = type)
        loadMockData()
    }

    private fun updateFilterPlate(plate: String?) {
        _uiState.value = _uiState.value.copy(filterPlate = plate)
        loadMockData()
    }

    private fun updateFilterDriver(driver: String?) {
        _uiState.value = _uiState.value.copy(filterDriver = driver)
        loadMockData()
    }

    private fun updateFilterVehicle(vehicle: String?) {
        _uiState.value = _uiState.value.copy(filterVehicle = vehicle)
        loadMockData()
    }

    private fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            filterPlate = null,
            filterDriver = null,
            filterVehicle = null
        )
        loadMockData()
    }

    private fun exportReport(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val filename = "rapor_${LocalDate.now()}.png"
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                context.contentResolver.let { resolver ->
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    uri?.let { imageUri ->
                        resolver.openOutputStream(imageUri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }
                        _uiEvent.emit(UiEvent.ShowSnackbar("Rapor kaydedildi: $filename"))
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Rapor kaydedilemedi: ${e.message}"))
            }
        }
    }

    private fun loadMockData() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, isEmpty = false)
            delay(450)
            val state = _uiState.value
            data class DataSet(
                val kpiData: KpiData,
                val attendanceData: List<ChartData>,
                val hourlyData: List<ChartData>,
                val trendData: List<ChartData>,
                val stackedBarData: List<com.ozyuce.maps.feature.reports.components.charts.StackedBarData>,
                val companyDistribution: List<com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution>,
                val detailTableData: List<com.ozyuce.maps.feature.reports.components.ReportDetailRow>
            )

            val dataSet = when (state.selectedFilterType) {
                FilterType.DAY -> DataSet(
                    kpiData = KpiData(
                        totalPersonnel = 156,
                        onBoard = 132,
                        absent = 24,
                        late = 6,
                        duration = "8s 30dk",
                        distance = "125 km",
                        totalPersonnelTrend = 4.2f,
                        onBoardTrend = 3.1f,
                        absentTrend = -1.4f,
                        lateTrend = -0.8f,
                        durationTrend = 2.3f,
                        distanceTrend = 1.9f
                    ),
                    attendanceData = listOf(
                        ChartData("Binen", 132, 0xFF10B981),
                        ChartData("Binmeyen", 24, 0xFFEF4444)
                    ),
                    hourlyData = listOf(
                        ChartData("08:00", 25, 0xFF10B981),
                        ChartData("09:00", 45, 0xFF10B981),
                        ChartData("10:00", 30, 0xFF10B981),
                        ChartData("11:00", 32, 0xFF10B981)
                    ),
                    trendData = listOf(
                        ChartData("08:00", 28, 0xFF10B981),
                        ChartData("09:00", 32, 0xFF10B981),
                        ChartData("10:00", 25, 0xFF10B981),
                        ChartData("11:00", 35, 0xFF10B981),
                        ChartData("12:00", 30, 0xFF10B981),
                        ChartData("13:00", 40, 0xFF10B981),
                        ChartData("14:00", 38, 0xFF10B981),
                        ChartData("15:00", 42, 0xFF10B981)
                    ),
                    stackedBarData = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Pzt", 60, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Sal", 72, 18),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Çar", 50, 30),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Per", 80, 15),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Cum", 66, 24),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Cmt", 54, 26),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Paz", 70, 20)
                    ),
                    companyDistribution = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ACME Gıda", 80),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Vento A.Ş.", 70),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ÖzgünTek", 60),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Mavi Lojistik", 50),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Delta Kimya", 40)
                    ),
                    detailTableData = listOf(
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H1", 101, 21, "8s1dk", "121 km"),
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H2", 102, 22, "8s2dk", "122 km"),
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H3", 103, 23, "8s3dk", "123 km"),
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H4", 104, 24, "8s4dk", "124 km"),
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H5", 105, 25, "8s5dk", "125 km")
                    )
                )
                FilterType.WEEK -> DataSet(
                    kpiData = KpiData(
                        totalPersonnel = 980,
                        onBoard = 865,
                        absent = 115,
                        late = 38,
                        duration = "52s 10dk",
                        distance = "845 km",
                        totalPersonnelTrend = 5.4f,
                        onBoardTrend = 4.6f,
                        absentTrend = -2.1f,
                        lateTrend = -1.2f,
                        durationTrend = 3.7f,
                        distanceTrend = 4.1f
                    ),
                    attendanceData = listOf(
                        ChartData("Binen", 865, 0xFF10B981),
                        ChartData("Binmeyen", 115, 0xFFEF4444)
                    ),
                    hourlyData = listOf(
                        ChartData("Pzt", 180, 0xFF10B981),
                        ChartData("Sal", 210, 0xFF10B981),
                        ChartData("Çar", 195, 0xFF10B981),
                        ChartData("Per", 205, 0xFF10B981),
                        ChartData("Cum", 190, 0xFF10B981)
                    ),
                    trendData = listOf(
                        ChartData("Pzt", 180, 0xFF10B981),
                        ChartData("Sal", 210, 0xFF10B981),
                        ChartData("Çar", 195, 0xFF10B981),
                        ChartData("Per", 205, 0xFF10B981),
                        ChartData("Cum", 190, 0xFF10B981)
                    ),
                    stackedBarData = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Pzt", 160, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Sal", 190, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Çar", 175, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Per", 185, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Cum", 170, 20),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Cmt", 150, 30),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("Paz", 145, 25)
                    ),
                    companyDistribution = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ACME Gıda", 85),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Vento A.Ş.", 75),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ÖzgünTek", 65),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Mavi Lojistik", 55),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Delta Kimya", 45)
                    ),
                    detailTableData = listOf(
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("ACME Gıda", "29.09", "H1", 180, 20, "52s", "845 km"),
                        com.ozyuce.maps.feature.reports.components.ReportDetailRow("Vento A.Ş.", "29.09", "H2", 175, 25, "48s", "780 km")
                    )
                )
                FilterType.MONTH -> DataSet(
                    kpiData = KpiData(
                        totalPersonnel = 4120,
                        onBoard = 3635,
                        absent = 485,
                        late = 152,
                        duration = "224s 40dk",
                        distance = "3.420 km",
                        totalPersonnelTrend = 6.8f,
                        onBoardTrend = 5.9f,
                        absentTrend = -1.9f,
                        lateTrend = -1.1f,
                        durationTrend = 4.3f,
                        distanceTrend = 5.0f
                    ),
                    attendanceData = listOf(
                        ChartData("Binen", 3635, 0xFF10B981),
                        ChartData("Binmeyen", 485, 0xFFEF4444)
                    ),
                    hourlyData = listOf(
                        ChartData("1. Hafta", 960, 0xFF10B981),
                        ChartData("2. Hafta", 1010, 0xFF10B981),
                        ChartData("3. Hafta", 890, 0xFF10B981),
                        ChartData("4. Hafta", 915, 0xFF10B981)
                    ),
                    trendData = listOf(
                        ChartData("1. Hafta", 960, 0xFF10B981),
                        ChartData("2. Hafta", 1010, 0xFF10B981),
                        ChartData("3. Hafta", 890, 0xFF10B981),
                        ChartData("4. Hafta", 915, 0xFF10B981)
                    ),
                    stackedBarData = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("1.H", 900, 60),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("2.H", 950, 60),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("3.H", 840, 50),
                        com.ozyuce.maps.feature.reports.components.charts.StackedBarData("4.H", 860, 55)
                    ),
                    companyDistribution = listOf(
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ACME Gıda", 88),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Vento A.Ş.", 78),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("ÖzgünTek", 68),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Mavi Lojistik", 58),
                        com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution("Delta Kimya", 48)
                    ),
                    detailTableData = emptyList()
                )
                FilterType.CUSTOM -> DataSet(
                    KpiData(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList()
                )
            }

            val plates = listOf("34 ABC 123", "34 XYZ 789", "06 OZY 456")
            val drivers = listOf("Ahmet Yılmaz", "Ayşe Demir", "Kerem Koç")
            val vehicleTypes = listOf("Mercedes Sprinter", "Ford Transit", "Volkswagen Crafter")
            val customers = listOf(
                "ACME Gıda",
                "Vento A.Ş.",
                "ÖzgünTek",
                "Mavi Lojistik",
                "Delta Kimya",
                "Kuzey Enerji",
                "Atlas Tekstil"
            )

            val hasData = listOf(state.filterPlate, state.filterDriver, state.filterVehicle).all { it == null }

            val updatedState = if (hasData) {
                state.copy(
                    kpiData = dataSet.kpiData,
                    attendanceData = dataSet.attendanceData,
                    hourlyData = dataSet.hourlyData,
                    trendData = dataSet.trendData,
                    stackedBarData = dataSet.stackedBarData,
                    companyDistribution = dataSet.companyDistribution,
                    detailTableData = dataSet.detailTableData,
                    totalStaff = dataSet.kpiData.totalPersonnel,
                    boardedCount = dataSet.kpiData.onBoard,
                    absentCount = dataSet.kpiData.absent,
                    totalDurationText = dataSet.kpiData.duration,
                    totalDistanceText = dataSet.kpiData.distance,
                    lateCount = dataSet.kpiData.late,
                    plates = plates,
                    drivers = drivers,
                    vehicleTypes = vehicleTypes,
                    customers = customers,
                    isLoading = false,
                    isEmpty = false
                )
            } else {
                state.copy(
                    kpiData = KpiData(),
                    attendanceData = emptyList(),
                    hourlyData = emptyList(),
                    trendData = emptyList(),
                    stackedBarData = emptyList(),
                    companyDistribution = emptyList(),
                    detailTableData = emptyList(),
                    totalStaff = 0,
                    boardedCount = 0,
                    absentCount = 0,
                    totalDurationText = "",
                    totalDistanceText = "",
                    lateCount = 0,
                    plates = plates,
                    drivers = drivers,
                    vehicleTypes = vehicleTypes,
                    customers = customers,
                    isLoading = false,
                    isEmpty = true
                )
            }

            _uiState.value = updatedState
        }
    }
}

data class ReportsUiState(
    val selectedFilterType: FilterType = FilterType.DAY,
    val selectedDateRange: DateRange = DateRange(LocalDate.now(), LocalDate.now()),
    val kpiData: KpiData = KpiData(),
    val attendanceData: List<ChartData> = emptyList(),
    val hourlyData: List<ChartData> = emptyList(),
    val trendData: List<ChartData> = emptyList(),
    val stackedBarData: List<com.ozyuce.maps.feature.reports.components.charts.StackedBarData> = emptyList(),
    val companyDistribution: List<com.ozyuce.maps.feature.reports.components.charts.CompanyDistribution> = emptyList(),
    val detailTableData: List<com.ozyuce.maps.feature.reports.components.ReportDetailRow> = emptyList(),
    val totalStaff: Int = 0,
    val boardedCount: Int = 0,
    val absentCount: Int = 0,
    val totalDurationText: String = "",
    val totalDistanceText: String = "",
    val lateCount: Int = 0,
    val plates: List<String> = emptyList(),
    val drivers: List<String> = emptyList(),
    val vehicleTypes: List<String> = emptyList(),
    val customers: List<String> = emptyList(),
    val selectedCustomers: List<String> = emptyList(),
    val filterPlate: String? = null,
    val filterDriver: String? = null,
    val filterVehicle: String? = null,
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

data class KpiData(
    val totalPersonnel: Int = 0,
    val onBoard: Int = 0,
    val absent: Int = 0,
    val late: Int = 0,
    val duration: String = "",
    val distance: String = "",
    val totalPersonnelTrend: Float? = null,
    val onBoardTrend: Float? = null,
    val absentTrend: Float? = null,
    val lateTrend: Float? = null,
    val durationTrend: Float? = null,
    val distanceTrend: Float? = null
)

data class ChartData(
    val label: String,
    val value: Int,
    val color: Long
)

data class DateRange(
    val start: LocalDate,
    val end: LocalDate
)

enum class FilterType {
    DAY, WEEK, MONTH, CUSTOM
}

sealed interface ReportsEvent {
    data class DateRangeChanged(val range: DateRange) : ReportsEvent
    data class FilterTypeChanged(val type: FilterType) : ReportsEvent
    data class ExportReport(val context: Context, val bitmap: Bitmap) : ReportsEvent
    data class FilterPlate(val plate: String?) : ReportsEvent
    data class FilterDriver(val driver: String?) : ReportsEvent
    data class FilterVehicle(val vehicle: String?) : ReportsEvent
    data class ToggleCustomer(val customer: String) : ReportsEvent
    data object ClearCustomers : ReportsEvent
    data object RefreshData : ReportsEvent
    data object ResetFilters : ReportsEvent
}

