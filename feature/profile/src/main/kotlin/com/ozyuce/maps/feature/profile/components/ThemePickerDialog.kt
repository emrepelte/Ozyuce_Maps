package com.ozyuce.maps.feature.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.profile.data.ThemeMode

@Composable
fun ThemePickerDialog(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tema Seçin") },
        text = {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.values().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = theme == selectedTheme,
                                onClick = { onThemeSelected(theme) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = when (theme) {
                                ThemeMode.SYSTEM -> "Sistem"
                                ThemeMode.LIGHT -> "Açık"
                                ThemeMode.DARK -> "Koyu"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}
