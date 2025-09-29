package com.ozyuce.maps.feature.stops.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.navigation.HandleUiEvents

@Composable
fun StopsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: StopsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleUiEvents(
        uiEvents = viewModel.events,
        navController = navController,
        snackbarHostState = snackbarHostState
    )

    var newPersonnelName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Duraklar", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        RouteHeader(state.routeId, onChange = viewModel::setRoute)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f, fill = true)) {
            items(state.stops) { stop ->
                StopRow(stop = stop, onBoarded = { viewModel.markStop(stop, true) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newPersonnelName,
            onValueChange = { newPersonnelName = it },
            label = { Text("Yeni personel") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.addPersonnel(newPersonnelName)
                newPersonnelName = ""
            },
            enabled = newPersonnelName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Personel Ekle")
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    SnackbarHost(hostState = snackbarHostState)
}

@Composable
private fun RouteHeader(routeId: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = routeId,
        onValueChange = onChange,
        label = { Text("Rota ID") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StopRow(stop: Stop, onBoarded: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(stop.name, style = MaterialTheme.typography.titleMedium)
            Text("Sıra: ${stop.sequence}", style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = onBoarded,
            enabled = stop.id.isNotBlank()
        ) {
            Text("Tamamlandı")
        }
    }
}

