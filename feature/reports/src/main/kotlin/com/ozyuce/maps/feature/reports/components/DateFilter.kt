package com.ozyuce.maps.feature.reports.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ozyuce.maps.feature.reports.DateRange
import com.ozyuce.maps.feature.reports.FilterType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateFilter(
    selectedType: FilterType,
    selectedRange: DateRange,
    onFilterTypeChanged: (FilterType) -> Unit,
    onDateRangeChanged: (DateRange) -> Unit,
    plates: List<String>,
    drivers: List<String>,
    vehicleTypes: List<String>,
    selectedPlate: String?,
    onPlate: (String?) -> Unit,
    selectedDriver: String?,
    onDriver: (String?) -> Unit,
    selectedVehicle: String?,
    onVehicle: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showCustomDateRange by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Filtre başlığı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rapor Filtreleri",
                    style = MaterialTheme.typography.titleMedium
                )
                
                TextButton(
                    onClick = { showCustomDateRange = !showCustomDateRange },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (showCustomDateRange) "Basit Filtre" else "Gelişmiş Filtre")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Filtre tipleri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { 
                            onFilterTypeChanged(type)
                            // Seçilen filtre tipine göre tarih aralığını ayarla
                            val today = LocalDate.now()
                            val newRange = when (type) {
                                FilterType.DAY -> DateRange(today, today)
                                FilterType.WEEK -> {
                                    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                    val endOfWeek = startOfWeek.plusDays(6)
                                    DateRange(startOfWeek, endOfWeek)
                                }
                                FilterType.MONTH -> {
                                    val startOfMonth = today.withDayOfMonth(1)
                                    val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                                    DateRange(startOfMonth, endOfMonth)
                                }
                                FilterType.CUSTOM -> null
                            }
                            if (type == FilterType.CUSTOM) {
                                showDatePicker = true
                            } else {
                                newRange?.let(onDateRangeChanged)
                            }
                        },
                        label = {
                            Text(
                                when (type) {
                                    FilterType.DAY -> "Gün"
                                    FilterType.WEEK -> "Hafta"
                                    FilterType.MONTH -> "Ay"
                                    FilterType.CUSTOM -> "Özel"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarih aralığı gösterimi
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedRange.start.format(dateFormatter)} - ${selectedRange.end.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Tarih seç",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Gelişmiş tarih aralığı seçimi
            AnimatedVisibility(
                visible = showCustomDateRange,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Özel Tarih Aralığı",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hazır tarih aralıkları
                        QuickDateRangeButton(
                            text = "Son 7 Gün",
                            onClick = {
                                val end = LocalDate.now()
                                val start = end.minusDays(6)
                                onDateRangeChanged(DateRange(start, end))
                            }
                        )
                        
                        QuickDateRangeButton(
                            text = "Son 30 Gün",
                            onClick = {
                                val end = LocalDate.now()
                                val start = end.minusDays(29)
                                onDateRangeChanged(DateRange(start, end))
                            }
                        )
                        
                        QuickDateRangeButton(
                            text = "Bu Çeyrek",
                            onClick = {
                                val today = LocalDate.now()
                                val month = today.monthValue
                                val quarter = (month - 1) / 3
                                val startMonth = quarter * 3 + 1
                                val start = LocalDate.of(today.year, startMonth, 1)
                                val end = start.plusMonths(3).minusDays(1)
                                onDateRangeChanged(DateRange(start, end))
                            }
                        )
                    }
                }
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdown(
                label = "Servis (Plaka)",
                options = plates,
                selected = selectedPlate,
                onSelected = onPlate,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            ExposedDropdown(
                label = "Şoför (Ad Soyad)",
                options = drivers,
                selected = selectedDriver,
                onSelected = onDriver,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            ExposedDropdown(
                label = "Araç (Marka/Model)",
                options = vehicleTypes,
                selected = selectedVehicle,
                onSelected = onVehicle,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
    
    // Tarih seçici dialog
    if (showDatePicker) {
        DateRangePickerDialog(
            initialDateRange = selectedRange,
            onDismiss = { showDatePicker = false },
            onDateRangeSelected = { 
                onDateRangeChanged(it)
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun QuickDateRangeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdown(
    label: String,
    options: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("(Tümü)") },
                onClick = {
                    onSelected(null)
                    expanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun DateRangePickerDialog(
    initialDateRange: DateRange,
    onDismiss: () -> Unit,
    onDateRangeSelected: (DateRange) -> Unit
) {
    var startDate by remember { mutableStateOf(initialDateRange.start) }
    var endDate by remember { mutableStateOf(initialDateRange.end) }
    var selectionMode by remember { mutableStateOf(SelectionMode.START) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(LocalDate.now())) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Dialog başlığı
                Text(
                    text = "Tarih Aralığı Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Seçilen tarih aralığı gösterimi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateRangeChip(
                        date = startDate,
                        isSelected = selectionMode == SelectionMode.START,
                        onClick = { selectionMode = SelectionMode.START },
                        label = "Başlangıç"
                    )
                    
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    DateRangeChip(
                        date = endDate,
                        isSelected = selectionMode == SelectionMode.END,
                        onClick = { selectionMode = SelectionMode.END },
                        label = "Bitiş"
                    )
                }
                
                // Ay navigasyonu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki ay")
                    }
                    
                    Text(
                        text = currentYearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("tr")) + " " + currentYearMonth.year,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki ay")
                    }
                }
                
                // Haftanın günleri başlıkları
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayOfWeek in DayOfWeek.values()) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("tr")).uppercase(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Takvim günleri
                val firstDayOfMonth = currentYearMonth.atDay(1)
                val lastDayOfMonth = currentYearMonth.atEndOfMonth()
                val firstDayOfGrid = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    var currentDay = firstDayOfGrid
                    for (week in 0 until 6) {
                        if (currentDay.isAfter(lastDayOfMonth)) break
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (day in 0 until 7) {
                                val isCurrentMonth = currentDay.month == currentYearMonth.month
                                val isSelected = (currentDay == startDate || currentDay == endDate)
                                val isInRange = (currentDay.isAfter(startDate) && currentDay.isBefore(endDate))
                                
                                CalendarDay(
                                    date = currentDay,
                                    isCurrentMonth = isCurrentMonth,
                                    isSelected = isSelected,
                                    isInRange = isInRange,
                                    isStartDate = currentDay == startDate,
                                    isEndDate = currentDay == endDate,
                                    onClick = {
                                        when (selectionMode) {
                                            SelectionMode.START -> {
                                                startDate = currentDay
                                                if (startDate.isAfter(endDate)) {
                                                    endDate = startDate
                                                }
                                                selectionMode = SelectionMode.END
                                            }
                                            SelectionMode.END -> {
                                                if (currentDay.isBefore(startDate)) {
                                                    endDate = startDate
                                                    startDate = currentDay
                                                } else {
                                                    endDate = currentDay
                                                }
                                                selectionMode = SelectionMode.START
                                            }
                                        }
                                    }
                                )
                                
                                currentDay = currentDay.plusDays(1)
                            }
                        }
                    }
                }
                
                // Butonlar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            onDateRangeSelected(DateRange(startDate, endDate))
                        }
                    ) {
                        Text("Uygula")
                    }
                }
            }
        }
    }
}

@Composable
private fun DateRangeChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = date.format(dateFormatter),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RowScope.CalendarDay(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isInRange: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isInRange -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = isCurrentMonth, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = when {
                !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

private enum class SelectionMode {
    START, END
}
