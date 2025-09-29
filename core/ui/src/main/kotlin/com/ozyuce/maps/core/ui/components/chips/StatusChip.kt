package com.ozyuce.maps.core.ui.components.chips

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class PersonStatus {
    ON_BOARD,
    ABSENT,
    PENDING
}

@Composable
fun StatusChip(
    status: PersonStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, text) = when (status) {
        PersonStatus.ON_BOARD -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Bindi"
        )
        PersonStatus.ABSENT -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Gelmedi"
        )
        PersonStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Bekliyor"
        )
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
