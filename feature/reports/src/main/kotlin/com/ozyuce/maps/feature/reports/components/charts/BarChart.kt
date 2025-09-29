package com.ozyuce.maps.feature.reports.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.feature.reports.ChartData

@Composable
fun BarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val maxValue = data.maxOf { it.value }.toFloat()
            val barWidth = size.width / data.size - 16f
            val bottomPadding = 24f

            data.forEachIndexed { index, item ->
                val barHeight = (item.value / maxValue) * (size.height - bottomPadding)
                val x = index * (barWidth + 16f)
                val y = size.height - barHeight - bottomPadding

                // Bar
                drawRect(
                    color = Color(item.color),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        // X ekseni etiketleri
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
