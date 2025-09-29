package com.ozyuce.maps.core.common.ui

sealed interface UiEvent {
    data class Navigate(val route: String) : UiEvent
    data class ShowSnackbar(val message: String) : UiEvent
}