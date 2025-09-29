package com.ozyuce.maps.core.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Minimum dokunma alani boyutu (48dp)
 */
@Composable
fun Modifier.accessibleClickable(
    onClick: () -> Unit,
    contentDescription: String,
    role: Role? = null
): Modifier = this
    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
    .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        role = role,
        onClick = onClick
    )
    .semantics {
        this.contentDescription = contentDescription
    }

/**
 * Erisilebilir padding (en az 8dp)
 */
fun Modifier.accessiblePadding(): Modifier = this
    .padding(8.dp)

/**
 * Dekoratif ogeler icin semantik temizleme
 */
fun Modifier.decorative(): Modifier = this
    .clearAndSetSemantics { }

/**
 * Ozel erisilebilirlik etiketi ekleme
 */
@Composable
fun Modifier.accessibilityLabel(
    label: String,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null
): Modifier = this.semantics {
    contentDescription = label
}.let { modifier ->
    if (isClickable && onClick != null) {
        modifier.accessibleClickable(
            onClick = onClick,
            contentDescription = label
        )
    } else {
        modifier
    }
}
