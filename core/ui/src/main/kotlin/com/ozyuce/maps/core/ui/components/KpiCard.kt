package com.ozyuce.maps.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozyuce.maps.core.designsystem.theme.OzyuceColors
import kotlin.math.abs

@Composable
fun KpiCard(
    icon: ImageVector,
    title: String,
    value: String,
    badge: (@Composable () -> Unit)? = null,
    trendPercent: Float? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // İkon ve Başlık
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = OzyuceColors.PrimarySoft
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = OzyuceColors.Primary
                            )
                        }
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                    
                    if (badge != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        badge()
                    }
                }

                // Değer
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Trend Badge (Sağ Üst)
            trendPercent?.let { percent ->
                val isPositive = percent > 0f
                val (bgColor, textColor) = if (isPositive) {
                    Color(0xFFE8FFF3) to Color(0xFF047857)
                } else {
                    Color(0xFFFFF0F0) to Color(0xFFB91C1C)
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.0f", percent)}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
