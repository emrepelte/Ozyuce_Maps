package com.ozyuce.maps.feature.stops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.ui.components.chips.PersonStatus
import com.ozyuce.maps.core.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StopsUiState())
    val uiState: StateFlow<StopsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var lastUpdatedPerson: PersonUiState? = null

    init {
        // TODO: Load initial data
        _uiState.value = StopsUiState(
            persons = List(10) { index ->
                PersonUiState(
                    id = index.toString(),
                    name = "Personel ${index + 1}",
                    department = "Departman ${(index % 3) + 1}",
                    status = when (index % 3) {
                        0 -> PersonStatus.ON_BOARD
                        1 -> PersonStatus.ABSENT
                        else -> PersonStatus.PENDING
                    }
                )
            }
        )
        updateCounters()
    }

    fun onEvent(event: StopsEvent) {
        when (event) {
            is StopsEvent.SearchQueryChanged -> updateSearchQuery(event.query)
            is StopsEvent.FilterChanged -> updateFilter(event.filter)
            is StopsEvent.TogglePersonStatus -> togglePersonStatus(event.personId)
            is StopsEvent.UndoStatusChange -> undoStatusChange()
            is StopsEvent.NavigateToAddPerson -> navigateToAddPerson()
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    private fun updateFilter(filter: PersonFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    private fun togglePersonStatus(personId: String) {
        val currentList = _uiState.value.persons.toMutableList()
        val personIndex = currentList.indexOfFirst { it.id == personId }
        
        if (personIndex != -1) {
            val person = currentList[personIndex]
            lastUpdatedPerson = person

            val newStatus = when (person.status) {
                PersonStatus.PENDING -> PersonStatus.ON_BOARD
                PersonStatus.ON_BOARD -> PersonStatus.ABSENT
                PersonStatus.ABSENT -> PersonStatus.ON_BOARD
            }

            currentList[personIndex] = person.copy(status = newStatus)
            _uiState.value = _uiState.value.copy(persons = currentList)
            updateCounters()

            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar(
                    message = "${person.name} durumu güncellendi",
                    actionLabel = "Geri Al",
                    onActionClick = { onEvent(StopsEvent.UndoStatusChange) }
                ))
            }
        }
    }

    private fun undoStatusChange() {
        lastUpdatedPerson?.let { person ->
            val currentList = _uiState.value.persons.toMutableList()
            val personIndex = currentList.indexOfFirst { it.id == person.id }
            
            if (personIndex != -1) {
                currentList[personIndex] = person
                _uiState.value = _uiState.value.copy(persons = currentList)
                updateCounters()
            }
        }
        lastUpdatedPerson = null
    }

    private fun navigateToAddPerson() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("add_person"))
        }
    }

    private fun updateCounters() {
        val persons = _uiState.value.persons
        _uiState.value = _uiState.value.copy(
            totalCount = persons.size,
            onBoardCount = persons.count { it.status == PersonStatus.ON_BOARD },
            remainingCount = persons.count { it.status != PersonStatus.ON_BOARD }
        )
    }
}

data class StopsUiState(
    val persons: List<PersonUiState> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: PersonFilter = PersonFilter.ALL,
    val totalCount: Int = 0,
    val onBoardCount: Int = 0,
    val remainingCount: Int = 0
) {
    val filteredPersons: List<PersonUiState>
        get() {
            val searchFiltered = if (searchQuery.isBlank()) {
                persons
            } else {
                persons.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            return when (selectedFilter) {
                PersonFilter.ALL -> searchFiltered
                PersonFilter.ON_BOARD -> searchFiltered.filter { it.status == PersonStatus.ON_BOARD }
                PersonFilter.REMAINING -> searchFiltered.filter { it.status != PersonStatus.ON_BOARD }
            }
        }
}

data class PersonUiState(
    val id: String,
    val name: String,
    val department: String,
    val status: PersonStatus
)

enum class PersonFilter {
    ALL, ON_BOARD, REMAINING
}

sealed interface StopsEvent {
    data class SearchQueryChanged(val query: String) : StopsEvent
    data class FilterChanged(val filter: PersonFilter) : StopsEvent
    data class TogglePersonStatus(val personId: String) : StopsEvent
    data object UndoStatusChange : StopsEvent
    data object NavigateToAddPerson : StopsEvent
}
