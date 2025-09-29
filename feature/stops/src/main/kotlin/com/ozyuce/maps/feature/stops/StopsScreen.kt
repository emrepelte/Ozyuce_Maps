package com.ozyuce.maps.feature.stops

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.events.UiEvent
import com.ozyuce.maps.feature.stops.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsScreen(
    onNavigate: (String) -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: StopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.onActionClick?.invoke()
                    }
                }
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Duraklar",
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(StopsEvent.NavigateToAddPerson) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Yeni Personel Ekle"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StopsSearchBar(
                searchQuery = uiState.searchQuery,
                selectedFilter = uiState.selectedFilter,
                onSearchQueryChange = { viewModel.onEvent(StopsEvent.SearchQueryChanged(it)) },
                onFilterChange = { viewModel.onEvent(StopsEvent.FilterChanged(it)) },
                modifier = Modifier.padding(16.dp)
            )

            StopsCounters(
                totalCount = uiState.totalCount,
                onBoardCount = uiState.onBoardCount,
                remainingCount = uiState.remainingCount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            PersonList(
                persons = uiState.filteredPersons,
                onPersonClick = { viewModel.onEvent(StopsEvent.TogglePersonStatus(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}
