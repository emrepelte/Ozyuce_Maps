package com.ozyuce.maps.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.designsystem.theme.ThemeMode
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.events.UiEvent
import com.ozyuce.maps.feature.dashboard.components.AnimatedWarningBanner
import com.ozyuce.maps.feature.dashboard.components.MiniMap
import com.ozyuce.maps.feature.dashboard.components.QuickActions
import com.ozyuce.maps.feature.dashboard.components.ServiceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    themeMode: ThemeMode,
    useDynamicColor: Boolean,
    onToggleThemeMode: () -> Unit,
    onToggleDynamicColor: () -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> {
                    // Snackbar üst düzeyde yönetilecek
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Ana Sayfa",
                showLogo = true,
                onProfileClick = onProfileClick,
                actions = {
                    FilledIconButton(
                        onClick = onToggleDynamicColor,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = if (useDynamicColor) {
                                "Dinamik rengi kapat"
                            } else {
                                "Dinamik rengi aç"
                            }
                        )
                    }
                    FilledIconButton(onClick = onToggleThemeMode) {
                        val icon = when (themeMode) {
                            ThemeMode.System, ThemeMode.Light -> Icons.Rounded.DarkMode
                            ThemeMode.Dark -> Icons.Rounded.LightMode
                        }
                        val description = when (themeMode) {
                            ThemeMode.System, ThemeMode.Light -> "Koyu temaya geç"
                            ThemeMode.Dark -> "Aydınlık temaya geç"
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = description
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            AnimatedWarningBanner(
                message = uiState.warningMessage,
                onDismiss = { viewModel.onEvent(DashboardEvent.DismissWarning) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ServiceCard(
                isActive = uiState.isServiceActive,
                duration = uiState.serviceDuration,
                plateNumber = uiState.plateNumber,
                routeName = uiState.routeName,
                onToggleService = { viewModel.onEvent(DashboardEvent.ToggleService) },
                modifier = Modifier.padding(top = 12.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            MiniMap(
                onMapClick = { onNavigate("map") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            QuickActions(
                onScanStop = { viewModel.onEvent(DashboardEvent.NavigateToStops) },
                onPassengerList = { viewModel.onEvent(DashboardEvent.NavigateToPassengers) },
                onReports = { viewModel.onEvent(DashboardEvent.NavigateToReports) },
                onPortal = { viewModel.onEvent(DashboardEvent.OpenPortal) },
                onArvento = { viewModel.onEvent(DashboardEvent.OpenArvento) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
