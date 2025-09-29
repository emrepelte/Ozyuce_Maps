package com.ozyuce.maps.feature.reports.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersSection(
    plates: List<String>,
    drivers: List<String>,
    vehicleTypes: List<String>,
    selectedPlate: String?, onPlate: (String?) -> Unit,
    selectedDriver: String?, onDriver: (String?) -> Unit,
    selectedVehicle: String?, onVehicle: (String?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdown("Servis (Plaka)", plates, selectedPlate, onPlate)
        ExposedDropdown("Şoför (Ad Soyad)", drivers, selectedDriver, onDriver)
        ExposedDropdown("Araç (Marka/Model)", vehicleTypes, selectedVehicle, onVehicle)
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



