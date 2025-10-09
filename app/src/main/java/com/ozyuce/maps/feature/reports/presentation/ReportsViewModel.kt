package com.ozyuce.maps.feature.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.reports.domain.GetDailyReportUseCase
import com.ozyuce.maps.feature.reports.domain.GetWeeklyReportUseCase
import com.ozyuce.maps.feature.reports.domain.GetReportChartsUseCase
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import com.ozyuce.maps.feature.reports.domain.model.ReportType
import com.ozyuce.maps.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Reports ViewModel - Raporlama ve analiz
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getDailyReportUseCase: GetDailyReportUseCase,
    private val getWeeklyReportUseCase: GetWeeklyReportUseCase,
    private val getReportChartsUseCase: GetReportChartsUseCase,
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val today = dateFormatter.format(Date())
        val routeId = "route_1" // Demo i?in sabit rota
        
        loadDailyReport(today, routeId)
        loadChartData(today, routeId)
    }

    fun selectReportType(reportType: ReportType) {
        _uiState.value = _uiState.value.copy(selectedReportType = reportType)
        
        when (reportType) {
            ReportType.DAILY -> {
                val today = dateFormatter.format(Date())
                loadDailyReport(today, "route_1")
            }
            ReportType.WEEKLY -> {
                val weekStart = getWeekStartDate()
                loadWeeklyReport(weekStart, "route_1")
            }
            ReportType.MONTHLY -> {
                // TODO: Monthly report implementation
                _uiEvent.tryEmit(UiEvent.ShowSnackbar("Ayl?k rapor ?zelli?i yak?nda eklenecek"))
            }
        }
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        
        when (_uiState.value.selectedReportType) {
            ReportType.DAILY -> loadDailyReport(date, "route_1")
            ReportType.WEEKLY -> loadWeeklyReport(date, "route_1")
            ReportType.MONTHLY -> { /* TODO */ }
        }
        
        loadChartData(date, "route_1")
    }

    private fun loadDailyReport(date: String, routeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = getDailyReportUseCase(date, routeId)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dailyReport = result.data,
                        selectedDate = date
                    )
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "G?nl?k rapor y?klenemedi: ${result.exception.message}"
                    )
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadWeeklyReport(weekStartDate: String, routeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = getWeeklyReportUseCase(weekStartDate, routeId)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weeklyReport = result.data
                    )
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Haftal?k rapor y?klenemedi: ${result.exception.message}"
                    )
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadChartData(date: String, routeId: String) {
        val filter = ReportFilter(
            startDate = date,
            endDate = date,
            routeId = routeId,
            reportType = _uiState.value.selectedReportType
        )
        
        viewModelScope.launch {
            // Load attendance chart
            when (val result = getReportChartsUseCase.getAttendanceChart(filter)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(attendanceChart = result.data)
                }
                is OzyuceResult.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Kat?l?m grafi?i y?klenemedi", isError = true))
                }
                is OzyuceResult.Loading -> { /* Handled by main loading */ }
            }
            
            // Load performance chart
            when (val result = getReportChartsUseCase.getPerformanceChart(filter)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(performanceChart = result.data)
                }
                is OzyuceResult.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Performans grafi?i y?klenemedi", isError = true))
                }
                is OzyuceResult.Loading -> { /* Handled by main loading */ }
            }
            
            // Load time analysis chart
            when (val result = getReportChartsUseCase.getTimeAnalysisChart(filter)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(timeAnalysisChart = result.data)
                }
                is OzyuceResult.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Zaman analizi grafi?i y?klenemedi", isError = true))
                }
                is OzyuceResult.Loading -> { /* Handled by main loading */ }
            }
        }
    }

    fun exportToPdf() {
        val dailyReport = _uiState.value.dailyReport ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            
            when (val result = reportsRepository.exportReportToPdf(dailyReport)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(isExporting = false)
                    _uiEvent.emit(UiEvent.ShowSnackbar("Rapor PDF olarak kaydedildi: ${result.data}"))
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(isExporting = false)
                    _uiEvent.emit(UiEvent.ShowSnackbar("PDF olu?turulamad?", isError = true))
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isExporting = true)
                }
            }
        }
    }

    fun refreshData() {
        val date = _uiState.value.selectedDate
        val routeId = "route_1"
        
        when (_uiState.value.selectedReportType) {
            ReportType.DAILY -> loadDailyReport(date, routeId)
            ReportType.WEEKLY -> loadWeeklyReport(date, routeId)
            ReportType.MONTHLY -> { /* TODO */ }
        }
        
        loadChartData(date, routeId)
    }

    private fun getWeekStartDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return dateFormatter.format(calendar.time)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Reports UI State
 */
data class ReportsUiState(
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val selectedReportType: ReportType = ReportType.DAILY,
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val dailyReport: DailyReport? = null,
    val weeklyReport: WeeklyReport? = null,
    val attendanceChart: ChartData? = null,
    val performanceChart: ChartData? = null,
    val timeAnalysisChart: ChartData? = null,
    val error: String? = null
)
