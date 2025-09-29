package com.ozyuce.maps.feature.reports.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.reports.ChartData

@Composable
fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val total = data.sumOf { it.value }
                if (total <= 0) return@Canvas

                var startAngle = 0f
                data.forEach { item ->
                    val sweepAngle = (item.value.toFloat() / total) * 360f
                    drawArc(
                        color = Color(item.color),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        style = Stroke(width = size.minDimension * 0.18f)
                    )
                    startAngle += sweepAngle
                }
            }

            Text(
                text = data.sumOf { it.value }.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                ChartLegendItem(
                    label = item.label,
                    value = item.value,
                    color = Color(item.color)
                )
            }
        }
    }
}

@Composable
private fun ChartLegendItem(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = MaterialTheme.shapes.small)
        )
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
