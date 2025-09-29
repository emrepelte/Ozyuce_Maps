package com.ozyuce.maps.core.ui.events

sealed interface UiEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onActionClick: (() -> Unit)? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : UiEvent

    data class Navigate(val route: String) : UiEvent
    data object NavigateUp : UiEvent
}

enum class SnackbarDuration {
    Short,
    Long,
    Indefinite
}
