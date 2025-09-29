package com.ozyuce.maps.feature.stops

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonScreen(
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Yeni Personel",
                navigateUp = onNavigateUp
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Yeni Personel Ekran?",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
