package com.ozyuce.maps.core.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun KpiCard(
    title: String,
    value: String,
    description: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // İkon varsa göster
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun PersonnelKpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    KpiCard(
        title = title,
        value = value,
        icon = when (title) {
            "Toplam Personel" -> Icons.Default.Person
            "Binen" -> Icons.Default.DirectionsWalk
            "Binmeyen" -> Icons.Default.PersonOff
            else -> Icons.Default.Person
        },
        iconTint = when (title) {
            "Toplam Personel" -> MaterialTheme.colorScheme.primary
            "Binen" -> Color(0xFF10B981) // Yeşil
            "Binmeyen" -> Color(0xFFEF4444) // Kırmızı
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = modifier
    )
}

@Composable
fun TimeDistanceKpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    KpiCard(
        title = title,
        value = value,
        icon = when (title) {
            "Toplam Süre" -> Icons.Default.AccessTime
            "Toplam Mesafe" -> Icons.Default.Route
            else -> null
        },
        iconTint = when (title) {
            "Toplam Süre" -> Color(0xFF6366F1) // Mor
            "Toplam Mesafe" -> Color(0xFF0EA5E9) // Mavi
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = modifier
    )
}
