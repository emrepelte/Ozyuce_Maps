package com.ozyuce.maps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.designsystem.theme.OzyuceTheme
import com.ozyuce.maps.core.designsystem.theme.ThemeMode

@Composable
fun ThemeTestScreen() {
    var themeMode by remember { mutableStateOf(ThemeMode.Light) }
    var useDynamicColor by remember { mutableStateOf(false) }

    val nextModeLabel = when (themeMode) {
        ThemeMode.System -> "Koyu Tema"
        ThemeMode.Light -> "Koyu Tema"
        ThemeMode.Dark -> "Sistem Temas?"
    }

    OzyuceTheme(themeMode = themeMode, useDynamicColor = useDynamicColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ozy?ce Maps",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "S?r?c? Bilgileri",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "G?nl?k rota ve durak detaylar?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = {
                    themeMode = when (themeMode) {
                        ThemeMode.System -> ThemeMode.Dark
                        ThemeMode.Dark -> ThemeMode.Light
                        ThemeMode.Light -> ThemeMode.System
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = nextModeLabel,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            OutlinedButton(
                onClick = { useDynamicColor = !useDynamicColor },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (useDynamicColor) "Dinamik Renk Kapal?" else "Dinamik Renk A??k",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
            }
        }
    }
}
