package com.ozyuce.maps.feature.service.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.navigation.HandleUiEvents

@Composable
fun ServiceScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleUiEvents(
        uiEvents = viewModel.events,
        navController = navController,
        snackbarHostState = snackbarHostState
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (state.isRunning) "Aktif Servis" else "Servis Başlat",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = state.selectedRouteId,
            onValueChange = viewModel::setRoute,
            enabled = !state.isLoading,
            label = { Text("Rota ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.startService(state.selectedRouteId) },
            enabled = !state.isLoading && state.selectedRouteId.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Servis Başlat")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = viewModel::endService,
            enabled = !state.isLoading && state.isRunning,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Servisi Bitir")
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    SnackbarHost(hostState = snackbarHostState)
}