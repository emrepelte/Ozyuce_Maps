package com.ozyuce.maps.feature.stops.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.stops.PersonFilter

@Composable
fun StopsSearchBar(
    searchQuery: String,
    selectedFilter: PersonFilter,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (PersonFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Durak Ara"
                )
            },
            placeholder = { Text("Personel ara...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("stops_search_field")
        )

        FilterChips(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun FilterChips(
    selectedFilter: PersonFilter,
    onFilterChange: (PersonFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == PersonFilter.ALL,
            onClick = { onFilterChange(PersonFilter.ALL) },
            label = { Text("Tümü") }
        )
        FilterChip(
            selected = selectedFilter == PersonFilter.ON_BOARD,
            onClick = { onFilterChange(PersonFilter.ON_BOARD) },
            label = { Text("Binen") }
        )
        FilterChip(
            selected = selectedFilter == PersonFilter.REMAINING,
            onClick = { onFilterChange(PersonFilter.REMAINING) },
            label = { Text("Kalan") }
        )
    }
}
