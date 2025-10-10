package com.ozyuce.maps.feature.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozyuce.maps.core.designsystem.theme.OzyuceColors

@Composable
fun QuickActions(
    onScanStop: () -> Unit,
    onPassengerList: () -> Unit,
    onReports: () -> Unit,
    onPortal: () -> Unit,
    onArvento: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Hızlı Eylemler başlığı
        Text(
            text = "Hızlı Eylemler",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Hızlı Eylemler - Dikey Liste
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                icon = Icons.Rounded.QrCodeScanner,
                label = "Durak Tara",
                onClick = onScanStop
            )
            QuickActionItem(
                icon = Icons.Rounded.People,
                label = "Yolcu Listesi",
                onClick = onPassengerList
            )
            QuickActionItem(
                icon = Icons.Rounded.BarChart,
                label = "Raporlar",
                onClick = onReports
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kestirmeler başlığı
        Text(
            text = "Kestirmeler",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Kestirmeler - 2'li Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                icon = Icons.Rounded.Web,
                label = "Portal",
                onClick = onPortal,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                icon = Icons.Rounded.LocationOn,
                label = "Arvento",
                onClick = onArvento,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "action_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        isPressed = true
                        onClick()
                        isPressed = false
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // İkon Container
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = OzyuceColors.PrimarySoft
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp),
                        tint = OzyuceColors.Primary
                    )
                }
            }
            
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
