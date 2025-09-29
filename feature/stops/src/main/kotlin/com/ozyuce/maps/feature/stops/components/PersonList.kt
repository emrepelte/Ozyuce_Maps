package com.ozyuce.maps.feature.stops.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.items.PersonListItem
import com.ozyuce.maps.feature.stops.PersonUiState

@Composable
fun PersonList(
    persons: List<PersonUiState>,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(
            items = persons,
            key = { it.id }
        ) { person ->
            PersonListItem(
                name = person.name,
                department = person.department,
                status = person.status,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("person_item_${person.id}")
                    .clickable { onPersonClick(person.id) }
            )
            Divider()
        }
    }
}
