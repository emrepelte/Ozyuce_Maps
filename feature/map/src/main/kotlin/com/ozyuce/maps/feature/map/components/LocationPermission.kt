package com.ozyuce.maps.feature.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(
    onPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(permissionState.status) {
        if (permissionState.status == PermissionStatus.Granted) {
            onPermissionGranted()
        }
    }

    when (permissionState.status) {
        is PermissionStatus.Granted -> {
            // İzin verildi, içerik gösterilecek
        }
        is PermissionStatus.Denied -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Konum İzni Gerekli",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Araç konumunu görebilmek ve durakları takip edebilmek için konum izni gerekiyor.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Button(
                    onClick = { permissionState.launchPermissionRequest() },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text("İzin Ver")
                }
            }
        }
    }
}
