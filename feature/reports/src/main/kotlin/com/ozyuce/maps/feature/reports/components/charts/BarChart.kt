package com.ozyuce.maps.feature.reports.components.charts

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.reports.ChartData

@Composable
fun BarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.value }.takeIf { it > 0 }?.toFloat() ?: return
    val barAnimations = data.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, delayMillis = index * 80),
            label = "barAnim$index"
        ).value
    }
    val onSurface = MaterialTheme.colorScheme.onSurface
    val labelPaint = remember(onSurface) {
        Paint().apply {
            color = onSurface.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = 32f
            isAntiAlias = true
        }
    }
    val referencePaintColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val availableHeight = size.height - 32f
            val spacing = 16f
            val barWidth = (size.width - (spacing * (data.size + 1))) / data.size

            repeat(4) { step ->
                val fraction = (step + 1) / 4f
                val y = size.height - 32f - (availableHeight * fraction)
                drawLine(
                    color = referencePaintColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            data.forEachIndexed { index, item ->
                val progress = barAnimations[index]
                val barHeight = progress * (item.value / maxValue) * availableHeight
                val x = spacing + index * (barWidth + spacing)
                val y = size.height - 32f - barHeight

                drawRect(
                    color = Color(item.color),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    item.value.toString(),
                    x + barWidth / 2f,
                    y - 12f,
                    labelPaint
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
