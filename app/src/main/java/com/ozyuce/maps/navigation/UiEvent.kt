package com.ozyuce.maps.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow

/**
 * UI event'leri i?in sealed interface
 * ViewModel'dan UI'ya one-shot event'ler g?ndermek i?in kullan?l?r
 */
sealed interface UiEvent {
    
    // Navigation Events
    data class Navigate(val route: String) : UiEvent
    data class NavigateWithArgs(val route: String, val args: Map<String, Any>) : UiEvent
    data object NavigateBack : UiEvent
    data class PopUpTo(val route: String, val inclusive: Boolean = false) : UiEvent
    
    // UI Feedback Events
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : UiEvent
    data class ShowToast(val message: String) : UiEvent
    data class ShowDialog(val title: String, val message: String, val onConfirm: (() -> Unit)? = null) : UiEvent
    
    // Loading Events
    data object ShowLoading : UiEvent
    data object HideLoading : UiEvent
}

/**
 * UiEvent'leri handle etmek i?in yard?mc? fonksiyon
 */
@Composable
fun HandleUiEvents(
    uiEvents: Flow<UiEvent>,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
    LaunchedEffect(uiEvents) {
        uiEvents.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navController.navigate(event.route)
                is UiEvent.NavigateBack -> navController.popBackStack()
                is UiEvent.PopUpTo -> navController.navigate(event.route) {
                    popUpTo(event.route) { inclusive = event.inclusive }
                }
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                // Di?er event'ler i?in handler'lar buraya eklenecek
                else -> { /* Handle other events */ }
            }
        }
    }
}
