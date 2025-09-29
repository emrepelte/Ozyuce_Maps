package com.ozyuce.maps.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.designsystem.theme.OzyuceTheme
import com.ozyuce.maps.core.designsystem.theme.ThemeMode

@Composable
fun ThemePreviewContent() {
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
            modifier = Modifier.semantics {
                contentDescription = "Uygulama ba?l???"
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "?rnek kart bile?eni"
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "S?r?c? Bilgileri",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "G?nl?k rota ve durak detaylar?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = { },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .semantics {
                    contentDescription = "Servisi ba?lat butonu"
                }
        ) {
            Text(
                text = "Servisi Ba?lat",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(name = "Light Theme")
@Composable
fun ThemePreviewLight() {
    OzyuceTheme(themeMode = ThemeMode.Light) {
        ThemePreviewContent()
    }
}

@Preview(name = "Dark Theme")
@Composable
fun ThemePreviewDark() {
    OzyuceTheme(themeMode = ThemeMode.Dark) {
        ThemePreviewContent()
    }
}
