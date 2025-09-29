package com.ozyuce.maps.feature.stops.presentation

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import com.ozyuce.maps.feature.stops.domain.validation.PhoneNumberValidator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.feature.stops.domain.model.Stop
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.StopStatus
import com.ozyuce.maps.navigation.HandleUiEvents
import com.ozyuce.maps.ui.theme.OzyuceBlue
import com.ozyuce.maps.ui.theme.OzyuceOrange
import com.ozyuce.maps.ui.theme.SuccessGreen
import com.ozyuce.maps.ui.theme.ErrorRed

/**
 * Durak kontrol? ve personel y?netimi ekran?
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: StopsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddPersonnelDialog by remember { mutableStateOf(false) }

    // Handle UI events
    HandleUiEvents(
        uiEvents = viewModel.uiEvent,
        navController = navController,
        snackbarHostState = snackbarHostState
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (uiState.selectedStop != null && !uiState.isLoading) {
                FloatingActionButton(
                    onClick = { showAddPersonnelDialog = true },
                    containerColor = OzyuceBlue
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Personel Ekle")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Durak Kontrol?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Stops Row
                StopsRow(
                    stops = uiState.stops,
                    selectedStop = uiState.selectedStop,
                    onStopSelected = viewModel::selectStop,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Selected Stop Personnel
                uiState.selectedStop?.let { selectedStop ->
                    SelectedStopSection(
                        stop = selectedStop,
                        personnel = uiState.selectedStopPersonnel,
                        onPersonnelCheck = viewModel::checkPersonnel,
                        onCompleteStop = viewModel::completeStop,
                        modifier = Modifier.weight(1f)
                    )
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

    // Add Personnel Dialog
    if (showAddPersonnelDialog) {
        AddPersonnelDialog(
            onDismiss = { showAddPersonnelDialog = false },
            onAddPersonnel = { name, surname, phone ->
                viewModel.addPersonnel(name, surname, phone)
                showAddPersonnelDialog = false
            },
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun StopsRow(
    stops: List<Stop>,
    selectedStop: Stop?,
    onStopSelected: (Stop) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Duraklar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(stops) { stop ->
                StopCard(
                    stop = stop,
                    isSelected = selectedStop?.id == stop.id,
                    onClick = { onStopSelected(stop) }
                )
            }
        }
    }
}

@Composable
private fun StopCard(
    stop: Stop,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> OzyuceBlue.copy(alpha = 0.2f)
                stop.getStatus() == StopStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.1f)
                stop.getStatus() == StopStatus.IN_PROGRESS -> OzyuceOrange.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, OzyuceBlue)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Icon
            Icon(
                imageVector = when (stop.getStatus()) {
                    StopStatus.COMPLETED -> Icons.Rounded.Check
                    StopStatus.IN_PROGRESS -> Icons.Rounded.Info
                    StopStatus.PENDING -> Icons.Rounded.LocationOn
                },
                contentDescription = null,
                tint = when (stop.getStatus()) {
                    StopStatus.COMPLETED -> SuccessGreen
                    StopStatus.IN_PROGRESS -> OzyuceOrange
                    StopStatus.PENDING -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stop.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stop.estimatedArrivalTime ?: "Belirsiz",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (stop.personnelCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = stop.getCompletionPercentage() / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = when (stop.getStatus()) {
                        StopStatus.COMPLETED -> SuccessGreen
                        StopStatus.IN_PROGRESS -> OzyuceOrange
                        StopStatus.PENDING -> MaterialTheme.colorScheme.outline
                    }
                )

                Text(
                    text = "${stop.checkedPersonnelCount}/${stop.personnelCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectedStopSection(
    stop: Stop,
    personnel: List<Personnel>,
    onPersonnelCheck: (String, Boolean, String?) -> Unit,
    onCompleteStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Stop Info Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = OzyuceBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stop.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (stop.getStatus() == StopStatus.COMPLETED) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Tamamland?",
                            tint = SuccessGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (stop.description.isNotBlank()) {
                    Text(
                        text = stop.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (stop.personnelCount > 0) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = stop.getCompletionPercentage() / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = SuccessGreen
                    )

                    Text(
                        text = "?lerleme: ${stop.checkedPersonnelCount}/${stop.personnelCount} (%.0f%%)".format(stop.getCompletionPercentage()),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personnel List
        if (personnel.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Bu durakta henüz personel yok.\n+ butonunu kullanarak personel ekleyebilirsiniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            Text(
                text = "Personel Listesi",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(personnel) { person ->
                    PersonnelCard(
                        personnel = person,
                        onCheck = { isChecked ->
                            onPersonnelCheck(person.id, isChecked, null)
                        }
                    )
                }
            }
        }

        // Complete Stop Button
        if (personnel.isNotEmpty() && !stop.isCompleted) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCompleteStop,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen
                ),
                enabled = stop.checkedPersonnelCount == stop.personnelCount
            ) {
                Text("Durağı Tamamla")
            }
        }
    }
}

@Composable
private fun PersonnelCard(
    personnel: Personnel,
    onCheck: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (personnel.isChecked) {
                SuccessGreen.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = personnel.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (!personnel.phoneNumber.isNullOrBlank()) {
                    Text(
                        text = personnel.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (personnel.checkTime != null) {
                    Text(
                        text = "Kontrol: ${personnel.checkTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row {
                // Check Button
                IconButton(
                    onClick = { onCheck(true) },
                    modifier = Modifier
                        .background(
                            color = if (personnel.isChecked) SuccessGreen else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "İşaretle",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Uncheck Button
                IconButton(
                    onClick = { onCheck(false) },
                    modifier = Modifier
                        .background(
                            color = if (!personnel.isChecked) ErrorRed else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "İşareti Kaldır",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPersonnelDialog(
    onDismiss: () -> Unit,
    onAddPersonnel: (String, String, String?) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val isPhoneValid = remember(phoneNumber) { PhoneNumberValidator.validate(phoneNumber) }
    

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Yeni Personel Ekle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ad") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Soyad") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefon (Opsiyonel)") },
                    placeholder = { Text("05XX XXX XX XX") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isPhoneValid,
                    supportingText = {
                        Text(
                            text = if (!isPhoneValid) "Geçersiz telefon numarası" else "Örnek: 0555 123 45 67"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && surname.isNotBlank() && isPhoneValid) {
                                onAddPersonnel(
                                    name.trim(),
                                    surname.trim(),
                                    phoneNumber.takeIf { it.isNotBlank() }
                                )
                            }
                        },
                        enabled = !isLoading && name.isNotBlank() && surname.isNotBlank() && isPhoneValid
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Ekle")
                        }
                    }
                }
            }
        }
    }
}








