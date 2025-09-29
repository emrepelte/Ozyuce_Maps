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
import com.ozyuce.maps.feature.profile.data.Language

@Composable
fun LanguagePickerDialog(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dil Seçin") },
        text = {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Language.values().forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = language == selectedLanguage,
                                onClick = { onLanguageSelected(language) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == selectedLanguage,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = when (language) {
                                Language.TR -> "Türkçe"
                                Language.EN -> "English"
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
