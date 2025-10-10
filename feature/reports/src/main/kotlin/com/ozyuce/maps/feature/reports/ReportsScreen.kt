package com.ozyuce.maps.feature.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.designsystem.theme.OzyuceColors
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.events.UiEvent
import com.ozyuce.maps.core.ui.export.exportViewAsPdf
import com.ozyuce.maps.core.ui.export.exportViewAsPng
import com.ozyuce.maps.core.ui.export.renderComposableToView
import com.ozyuce.maps.core.ui.export.shareFile
import com.ozyuce.maps.feature.reports.components.DetailTable
import com.ozyuce.maps.feature.reports.components.KpiGrid
import com.ozyuce.maps.feature.reports.components.charts.CompanyDistributionChart
import com.ozyuce.maps.feature.reports.components.charts.LineChart
import com.ozyuce.maps.feature.reports.components.charts.StackedBarChart
import com.ozyuce.maps.feature.reports.ui.ReportPrintLayout
import com.ozyuce.maps.feature.reports.ui.ReportSection
import com.ozyuce.maps.feature.reports.ui.ShimmerReportPlaceholder
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

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
    
    var showCustomerSheet by remember { mutableStateOf(false) }
    var showAdvancedFilterSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var customerSearch by remember { mutableStateOf("") }

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
                showLogo = true,
                onProfileClick = onProfileClick,
                actions = {
                    FilledIconButton(
                        onClick = {
                            scope.launch {
                                val view = renderComposableToView(
                                    context = context,
                                    widthPx = 1240,
                                    heightPx = 1754
                                ) { ReportPrintLayout(uiState) }
                                val file = exportViewAsPdf(view, "rapor_${System.currentTimeMillis()}")
                                shareFile(context, file, "application/pdf", "Rapor Paylaş")
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Paylaş"
                        )
                    }
                    FilledIconButton(
                        onClick = {
                            scope.launch {
                                val view = renderComposableToView(
                                    context = context,
                                    widthPx = 1240,
                                    heightPx = 1754
                                ) { ReportPrintLayout(uiState) }
                                val file = exportViewAsPdf(view, "rapor_${System.currentTimeMillis()}")
                                shareFile(context, file, "application/pdf", "Rapor İndir")
                                snackbarHostState.showSnackbar("Rapor indirildi")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = "İndir"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Alt bar sadece filtre seçildiğinde gösterilir
            val hasActiveFilters = uiState.selectedCustomers.isNotEmpty() || 
                                   uiState.filterPlate != null || 
                                   uiState.filterDriver != null || 
                                   uiState.filterVehicle != null
            
            if (hasActiveFilters) {
                ActiveFiltersBar(
                    selectedCustomers = uiState.selectedCustomers,
                    selectedPlate = uiState.filterPlate,
                    selectedDriver = uiState.filterDriver,
                    selectedVehicle = uiState.filterVehicle,
                    onClearFilters = { 
                        viewModel.onEvent(ReportsEvent.ResetFilters)
                        viewModel.onEvent(ReportsEvent.ClearCustomers)
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    ShimmerReportPlaceholder(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Filtre Bölümü
                item {
                            FilterSection(
                        selectedType = uiState.selectedFilterType,
                                dateRange = uiState.selectedDateRange,
                                selectedCustomers = uiState.selectedCustomers,
                        onFilterTypeChanged = { viewModel.onEvent(ReportsEvent.FilterTypeChanged(it)) },
                                onDateRangeClick = { showDatePicker = true },
                                onCustomerClick = { showCustomerSheet = true },
                                onAdvancedClick = { showAdvancedFilterSheet = true },
                                onRemoveCustomer = { viewModel.onEvent(ReportsEvent.ToggleCustomer(it)) },
                                onClearCustomers = { viewModel.onEvent(ReportsEvent.ClearCustomers) }
                            )
                        }

                        // Özet Başlığı
                        item {
                            val isSingleCustomer = uiState.selectedCustomers.size == 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Özet",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                if (isSingleCustomer) {
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = "Firma: ${uiState.selectedCustomers.first()}",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = OzyuceColors.PrimarySoft,
                                            labelColor = OzyuceColors.Primary
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }

                        // KPI Grid
                        item {
                            KpiGrid(data = uiState.kpiData, modifier = Modifier)
                        }

                        // Trend & Dağılım Başlığı
                        item {
                            Text(
                                text = "Trend & Dağılım",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Line Chart - Binen Günlük Trend
                        item {
                            ReportSection(
                                title = "Binen – Günlük Trend",
                                content = {
                                    LineChart(
                                        data = uiState.trendData,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }

                        // Stacked Bar Chart - Binen/Binmeyen Haftalık
                        item {
                            ReportSection(
                                title = "Binen / Binmeyen (Haftalık)",
                                content = {
                                    StackedBarChart(
                                        data = uiState.stackedBarData,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }

                        // Firma Bazlı Dağılım (sadece müşteri seçilmemişse)
                        if (uiState.selectedCustomers.isEmpty() && uiState.companyDistribution.isNotEmpty()) {
                            item {
                                ReportSection(
                                    title = "Firma Bazlı Dağılım (İlk 5)",
                                    content = {
                                        CompanyDistributionChart(
                                            data = uiState.companyDistribution,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                )
                            }
                        }

                        // Detay Tablosu
                        if (uiState.detailTableData.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Detay Tablosu",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            item {
                                DetailTable(
                                    data = uiState.detailTableData,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Alt padding
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Müşteri Filtresi Bottom Sheet
    if (showCustomerSheet) {
        CustomerFilterSheet(
            customers = uiState.customers,
            selectedCustomers = uiState.selectedCustomers,
            searchQuery = customerSearch,
            onSearchQueryChange = { customerSearch = it },
            onToggleCustomer = { viewModel.onEvent(ReportsEvent.ToggleCustomer(it)) },
            onClearAll = { viewModel.onEvent(ReportsEvent.ClearCustomers) },
            onDismiss = { showCustomerSheet = false },
            onApply = { showCustomerSheet = false }
        )
    }

    // Gelişmiş Filtre Bottom Sheet
    if (showAdvancedFilterSheet) {
        AdvancedFilterSheet(
            plates = uiState.plates,
            drivers = uiState.drivers,
            vehicles = uiState.vehicleTypes,
            selectedPlate = uiState.filterPlate,
            selectedDriver = uiState.filterDriver,
            selectedVehicle = uiState.filterVehicle,
            onPlateSelected = { viewModel.onEvent(ReportsEvent.FilterPlate(it)) },
            onDriverSelected = { viewModel.onEvent(ReportsEvent.FilterDriver(it)) },
            onVehicleSelected = { viewModel.onEvent(ReportsEvent.FilterVehicle(it)) },
            onDismiss = { showAdvancedFilterSheet = false },
            onApply = { showAdvancedFilterSheet = false }
        )
    }

    // Tarih Seçici Dialog
    if (showDatePicker) {
        SimpleDatePickerDialog(
            currentRange = uiState.selectedDateRange,
            onDismiss = { showDatePicker = false },
            onDateSelected = { range ->
                viewModel.onEvent(ReportsEvent.DateRangeChanged(range))
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun FilterSection(
    selectedType: FilterType,
    dateRange: DateRange,
    selectedCustomers: List<String>,
    onFilterTypeChanged: (FilterType) -> Unit,
    onDateRangeClick: () -> Unit,
    onCustomerClick: () -> Unit,
    onAdvancedClick: () -> Unit,
    onRemoveCustomer: (String) -> Unit,
    onClearCustomers: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filtre Butonları: Gün, Hafta, Ay, Özel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                FilterType.DAY to "Gün",
                FilterType.WEEK to "Hafta",
                FilterType.MONTH to "Ay",
                FilterType.CUSTOM to "Özel"
            ).forEach { (type, label) ->
                FilterButton(
                    text = label,
                    isSelected = selectedType == type,
                    onClick = { onFilterTypeChanged(type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tarih, Müşteri, Gelişmiş Filtreleri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tarih Seçici
            Surface(
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = onDateRangeClick
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dateRange.start.format(dateFormatter)} – ${dateRange.end.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Müşteri Filtresi
            Surface(
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = onCustomerClick
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Müşteri",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (selectedCustomers.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = OzyuceColors.Accent
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedCustomers.size.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Gelişmiş Filtre
            Surface(
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = onAdvancedClick
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Gelişmiş",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Seçili Müşteri Chip'leri
        if (selectedCustomers.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedCustomers.take(3).forEach { customer ->
                    AssistChip(
                        onClick = {},
                        label = { Text(customer, style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveCustomer(customer) }
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OzyuceColors.PrimarySoft,
                            labelColor = OzyuceColors.Primary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    )
                }
                if (selectedCustomers.size > 3) {
                    Text(
                        text = "+${selectedCustomers.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onClearCustomers) {
                    Text("Temizle", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) OzyuceColors.Primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerFilterSheet(
    customers: List<String>,
    selectedCustomers: List<String>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleCustomer: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter { it.contains(searchQuery, ignoreCase = true) }
    }

        ModalBottomSheet(
        onDismissRequest = onDismiss,
            sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Müşteri Filtrele",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Kapat")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Müşteri ara", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredCustomers.chunked(2).forEach { rowCustomers ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCustomers.forEach { customer ->
                            val isSelected = selectedCustomers.contains(customer)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) OzyuceColors.Primary else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                onClick = { onToggleCustomer(customer) }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = customer,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                        if (rowCustomers.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClearAll) {
                    Text("Temizle", style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OzyuceColors.Primary
                        )
                    ) {
                        Text("Uygula")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedFilterSheet(
    plates: List<String>,
    drivers: List<String>,
    vehicles: List<String>,
    selectedPlate: String?,
    selectedDriver: String?,
    selectedVehicle: String?,
    onPlateSelected: (String?) -> Unit,
    onDriverSelected: (String?) -> Unit,
    onVehicleSelected: (String?) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gelişmiş Filtreler",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Kapat")
                }
            }

            // Plaka Dropdown
            FilterDropdown(
                label = "Servis (Plaka)",
                options = plates,
                selected = selectedPlate,
                onSelected = onPlateSelected
            )

            // Şoför Dropdown
            FilterDropdown(
                label = "Şoför (Ad Soyad)",
                options = drivers,
                selected = selectedDriver,
                onSelected = onDriverSelected
            )

            // Araç Dropdown
            FilterDropdown(
                label = "Araç (Marka/Model)",
                options = vehicles,
                selected = selectedVehicle,
                onSelected = onVehicleSelected
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OzyuceColors.Primary
                        )
                    ) {
                        Text("Uygula")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected ?: "Tümü",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tümü") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveFiltersBar(
    selectedCustomers: List<String>,
    selectedPlate: String?,
    selectedDriver: String?,
    selectedVehicle: String?,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Aktif Filtreler Başlığı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktif Filtreler",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                TextButton(onClick = onClearFilters) {
                    Text("Filtreleri Temizle", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Filtre Chip'leri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedCustomers.forEach { customer ->
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(
                                "Müşteri: $customer",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OzyuceColors.PrimarySoft,
                            labelColor = OzyuceColors.Primary
                        )
                    )
                }
                
                selectedPlate?.let { plate ->
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(
                                "Plaka: $plate",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OzyuceColors.PrimarySoft,
                            labelColor = OzyuceColors.Primary
                        )
                    )
                }
                
                selectedDriver?.let { driver ->
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(
                                "Şoför: $driver",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OzyuceColors.PrimarySoft,
                            labelColor = OzyuceColors.Primary
                        )
                    )
                }
                
                selectedVehicle?.let { vehicle ->
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(
                                "Araç: $vehicle",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OzyuceColors.PrimarySoft,
                            labelColor = OzyuceColors.Primary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDatePickerDialog(
    currentRange: DateRange,
    onDismiss: () -> Unit,
    onDateSelected: (DateRange) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = currentRange.start.toEpochDay() * 24 * 60 * 60 * 1000,
        initialSelectedEndDateMillis = currentRange.end.toEpochDay() * 24 * 60 * 60 * 1000
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis
                    
                    if (startMillis != null && endMillis != null) {
                        val startDate = java.time.Instant.ofEpochMilli(startMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        val endDate = java.time.Instant.ofEpochMilli(endMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(DateRange(startDate, endDate))
                    }
                }
            ) {
                Text("Tamam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Tarih Aralığı Seçin",
                    modifier = Modifier.padding(16.dp)
                )
            },
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                        val startDate = java.time.Instant.ofEpochMilli(startMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        Text(
                            text = startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(" - ")
                    dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                        val endDate = java.time.Instant.ofEpochMilli(endMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        Text(
                            text = endDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        )
    }
}
