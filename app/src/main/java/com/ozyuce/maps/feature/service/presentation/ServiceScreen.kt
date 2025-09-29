package com.ozyuce.maps.feature.service.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.feature.service.domain.model.Route
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import com.ozyuce.maps.feature.service.domain.model.ServiceStatus
import com.ozyuce.maps.navigation.HandleUiEvents
import com.ozyuce.maps.ui.theme.OzyuceBlue
import com.ozyuce.maps.ui.theme.OzyuceOrange
import com.ozyuce.maps.ui.theme.SuccessGreen
import com.ozyuce.maps.ui.theme.ErrorRed

/**
 * Servis y?netimi ekran?
 */
@Composable
fun ServiceScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: ServiceViewModel = hiltViewModel(),
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
        // Header
        Text(
            text = "Servis Y?netimi",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Service Status Card
        ServiceStatusCard(
            serviceStatus = uiState.serviceStatus,
            currentSession = uiState.currentSession,
            isLoading = uiState.isLoading,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Main Action Area
        when (uiState.serviceStatus) {
            ServiceStatus.IDLE -> {
                RouteSelectionSection(
                    availableRoutes = uiState.availableRoutes,
                    selectedRoute = uiState.selectedRoute,
                    onRouteSelected = viewModel::selectRoute,
                    onStartService = { route ->
                        viewModel.startService(route.id)
                    },
                    isLoading = uiState.isLoading
                )
            }
            ServiceStatus.ACTIVE -> {
                ActiveServiceSection(
                    session = uiState.currentSession,
                    onEndService = viewModel::endService,
                    isLoading = uiState.isLoading
                )
            }
            ServiceStatus.COMPLETED -> {
                CompletedServiceSection(
                    session = uiState.currentSession
                )
            }
            ServiceStatus.PAUSED -> {
                // TODO: Implement pause/resume functionality
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
private fun ServiceStatusCard(
    serviceStatus: ServiceStatus,
    currentSession: ServiceSession?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (serviceStatus) {
                ServiceStatus.IDLE -> MaterialTheme.colorScheme.surfaceVariant
                ServiceStatus.ACTIVE -> SuccessGreen.copy(alpha = 0.1f)
                ServiceStatus.COMPLETED -> OzyuceBlue.copy(alpha = 0.1f)
                ServiceStatus.PAUSED -> OzyuceOrange.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = when (serviceStatus) {
                            ServiceStatus.IDLE -> MaterialTheme.colorScheme.outline
                            ServiceStatus.ACTIVE -> SuccessGreen
                            ServiceStatus.COMPLETED -> OzyuceBlue
                            ServiceStatus.PAUSED -> OzyuceOrange
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        imageVector = when (serviceStatus) {
                            ServiceStatus.IDLE -> Icons.Rounded.PlayArrow
                            ServiceStatus.ACTIVE -> Icons.Rounded.Done
                            ServiceStatus.COMPLETED -> Icons.Rounded.Done
                            ServiceStatus.PAUSED -> Icons.Rounded.PlayArrow
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Text
            Text(
                text = when (serviceStatus) {
                    ServiceStatus.IDLE -> "Servis Beklemede"
                    ServiceStatus.ACTIVE -> "Servis Aktif"
                    ServiceStatus.COMPLETED -> "Servis Tamamland?"
                    ServiceStatus.PAUSED -> "Servis Duraklat?ld?"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Session Info
            currentSession?.let { session ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "S?re: ${session.getDurationMinutes()} dakika",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (session.totalStops > 0) {
                    Text(
                        text = "?lerleme: ${session.stopsCompleted}/${session.totalStops} durak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    LinearProgressIndicator(
                        progress = session.getCompletionPercentage() / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteSelectionSection(
    availableRoutes: List<Route>,
    selectedRoute: Route?,
    onRouteSelected: (Route) -> Unit,
    onStartService: (Route) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Rota Se?in",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Route Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && !isLoading },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedRoute?.name ?: "Rota se?iniz...",
                onValueChange = {},
                readOnly = true,
                enabled = !isLoading,
                label = { Text("Aktif Rota") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Build, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableRoutes.forEach { route ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = route.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${route.totalStops} durak ? ${route.estimatedDuration} dk",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onRouteSelected(route)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start Button
        Button(
            onClick = {
                selectedRoute?.let { onStartService(it) }
            },
            enabled = selectedRoute != null && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isLoading) "Ba?lat?l?yor..." else "Servisi Ba?lat",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ActiveServiceSection(
    session: ServiceSession?,
    onEndService: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        session?.let { activeSession ->
            // Active Session Stats
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    StatCard(
                        icon = Icons.Rounded.Info,
                        title = "Aktif S?re",
                        value = "${activeSession.getDurationMinutes()} dakika",
                        color = OzyuceBlue
                    )
                }
                
                if (activeSession.totalDistance > 0) {
                    item {
                        StatCard(
                            icon = Icons.Rounded.Build,
                            title = "Mesafe",
                            value = "%.1f km".format(activeSession.totalDistance),
                            color = OzyuceOrange
                        )
                    }
                }
                
                if (activeSession.averageSpeed > 0) {
                    item {
                        StatCard(
                            icon = Icons.Rounded.Check,
                            title = "Ortalama H?z",
                            value = "%.0f km/h".format(activeSession.averageSpeed),
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // End Service Button
        Button(
            onClick = onEndService,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isLoading) "Bitiriliyor..." else "Servisi Bitir",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun CompletedServiceSection(
    session: ServiceSession?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "? Servis Ba?ar?yla Tamamland?!",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = SuccessGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        session?.let { completedSession ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Toplam S?re:", fontWeight = FontWeight.Medium)
                        Text("${completedSession.getDurationMinutes()} dakika")
                    }
                    
                    if (completedSession.totalDistance > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Mesafe:", fontWeight = FontWeight.Medium)
                            Text("%.1f km".format(completedSession.totalDistance))
                        }
                    }
                    
                    if (completedSession.averageSpeed > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ortalama H?z:", fontWeight = FontWeight.Medium)
                            Text("%.0f km/h".format(completedSession.averageSpeed))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
