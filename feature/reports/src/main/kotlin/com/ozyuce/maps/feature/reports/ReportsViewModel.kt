package com.ozyuce.maps.feature.reports

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
            is ReportsEvent.RefreshData -> loadMockData()
        }
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
        val state = _uiState.value
        val (kpiData, attendanceData, hourlyData) = when (state.selectedFilterType) {
            FilterType.DAY -> Triple(
                KpiData(
                    totalPersonnel = 156,
                    onBoard = 132,
                    absent = 24,
                    late = 6,
                    duration = "8s 30dk",
                    distance = "125 km"
                ),
                listOf(
                    ChartData("Binen", 132, 0xFF10B981),
                    ChartData("Binmeyen", 24, 0xFFEF4444)
                ),
                listOf(
                    ChartData("08:00", 25, 0xFF10B981),
                    ChartData("09:00", 45, 0xFF10B981),
                    ChartData("10:00", 30, 0xFF10B981),
                    ChartData("11:00", 32, 0xFF10B981)
                )
            )
            FilterType.WEEK -> Triple(
                KpiData(
                    totalPersonnel = 980,
                    onBoard = 865,
                    absent = 115,
                    late = 38,
                    duration = "52s 10dk",
                    distance = "845 km"
                ),
                listOf(
                    ChartData("Binen", 865, 0xFF10B981),
                    ChartData("Binmeyen", 115, 0xFFEF4444)
                ),
                listOf(
                    ChartData("Pzt", 180, 0xFF10B981),
                    ChartData("Sal", 210, 0xFF10B981),
                    ChartData("Çar", 195, 0xFF10B981),
                    ChartData("Per", 205, 0xFF10B981),
                    ChartData("Cum", 190, 0xFF10B981)
                )
            )
            FilterType.MONTH -> Triple(
                KpiData(
                    totalPersonnel = 4120,
                    onBoard = 3635,
                    absent = 485,
                    late = 152,
                    duration = "224s 40dk",
                    distance = "3.420 km"
                ),
                listOf(
                    ChartData("Binen", 3635, 0xFF10B981),
                    ChartData("Binmeyen", 485, 0xFFEF4444)
                ),
                listOf(
                    ChartData("1. Hafta", 960, 0xFF10B981),
                    ChartData("2. Hafta", 1010, 0xFF10B981),
                    ChartData("3. Hafta", 890, 0xFF10B981),
                    ChartData("4. Hafta", 915, 0xFF10B981)
                )
            )
        }

        val plates = listOf("34 ABC 123", "34 XYZ 789", "06 OZY 456")
        val drivers = listOf("Ahmet Yılmaz", "Ayşe Demir", "Kerem Koç")
        val vehicleTypes = listOf("Mercedes Sprinter", "Ford Transit", "Volkswagen Crafter")

        _uiState.value = state.copy(
            kpiData = kpiData,
            attendanceData = attendanceData,
            hourlyData = hourlyData,
            totalStaff = kpiData.totalPersonnel,
            boardedCount = kpiData.onBoard,
            absentCount = kpiData.absent,
            totalDurationText = kpiData.duration,
            totalDistanceText = kpiData.distance,
            lateCount = kpiData.late,
            plates = plates,
            drivers = drivers,
            vehicleTypes = vehicleTypes
        )
    }
}

data class ReportsUiState(
    val selectedFilterType: FilterType = FilterType.DAY,
    val selectedDateRange: DateRange = DateRange(LocalDate.now(), LocalDate.now()),
    val kpiData: KpiData = KpiData(),
    val attendanceData: List<ChartData> = emptyList(),
    val hourlyData: List<ChartData> = emptyList(),
    val totalStaff: Int = 0,
    val boardedCount: Int = 0,
    val absentCount: Int = 0,
    val totalDurationText: String = "",
    val totalDistanceText: String = "",
    val lateCount: Int = 0,
    val plates: List<String> = emptyList(),
    val drivers: List<String> = emptyList(),
    val vehicleTypes: List<String> = emptyList(),
    val filterPlate: String? = null,
    val filterDriver: String? = null,
    val filterVehicle: String? = null
)

data class KpiData(
    val totalPersonnel: Int = 0,
    val onBoard: Int = 0,
    val absent: Int = 0,
    val late: Int = 0,
    val duration: String = "",
    val distance: String = ""
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
    DAY, WEEK, MONTH
}

sealed interface ReportsEvent {
    data class DateRangeChanged(val range: DateRange) : ReportsEvent
    data class FilterTypeChanged(val type: FilterType) : ReportsEvent
    data class ExportReport(val context: Context, val bitmap: Bitmap) : ReportsEvent
    data class FilterPlate(val plate: String?) : ReportsEvent
    data class FilterDriver(val driver: String?) : ReportsEvent
    data class FilterVehicle(val vehicle: String?) : ReportsEvent
    data object RefreshData : ReportsEvent
}




