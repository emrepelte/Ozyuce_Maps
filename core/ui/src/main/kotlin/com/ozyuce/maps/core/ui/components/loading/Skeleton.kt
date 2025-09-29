package com.ozyuce.maps.core.ui.components.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        )

        val transition = rememberInfiniteTransition(label = "skeleton")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "skeleton"
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnim - 1000f, y = 0f),
            end = Offset(x = translateAnim, y = 1000f)
        )

        Box(
            modifier = modifier
                .background(
                    brush = brush,
                    shape = MaterialTheme.shapes.small
                )
        )
    } else {
        content()
    }
}
