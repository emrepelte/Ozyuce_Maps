package com.ozyuce.maps.feature.map.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.ozyuce.maps.feature.map.domain.model.MapStyle
import com.ozyuce.maps.feature.map.domain.model.MapTrackingMode
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import com.ozyuce.maps.navigation.HandleUiEvents
import com.ozyuce.maps.ui.theme.ErrorRed
import com.ozyuce.maps.ui.theme.OzyuceBlue
import com.ozyuce.maps.ui.theme.SuccessGreen
import com.ozyuce.maps.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.Locale
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleUiEvents(
        uiEvents = viewModel.uiEvent,
        navController = navController,
        snackbarHostState = snackbarHostState
    )

    var locationSecurityException by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(granted)
        if (granted && !uiState.isTracking) {
            viewModel.startTracking()
        }
    }

    LaunchedEffect(uiState.isLocationPermissionGranted) {
        if (!uiState.isLocationPermissionGranted) {
            locationSecurityException = false
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val granted = fineGranted || coarseGranted
        viewModel.onLocationPermissionResult(granted)
        if (!granted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(41.0082, 28.9784),
            12f
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Harita") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onMapStyleChanged(
                                when (uiState.mapStyle) {
                                    MapStyle.NORMAL -> MapStyle.SATELLITE
                                    MapStyle.SATELLITE -> MapStyle.TERRAIN
                                    MapStyle.TERRAIN -> MapStyle.HYBRID
                                    MapStyle.HYBRID -> MapStyle.NORMAL
                                }
                            )
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Place,
                            contentDescription = "Harita stilini değiştir",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onTrackingModeChanged(
                            when (uiState.trackingMode) {
                                MapTrackingMode.FOLLOW -> MapTrackingMode.FREE
                                MapTrackingMode.FREE -> MapTrackingMode.OVERVIEW
                                MapTrackingMode.OVERVIEW -> MapTrackingMode.FOLLOW
                            }
                        )
                    },
                    containerColor = when (uiState.trackingMode) {
                        MapTrackingMode.FOLLOW -> SuccessGreen
                        MapTrackingMode.FREE -> WarningYellow
                        MapTrackingMode.OVERVIEW -> OzyuceBlue
                    }
                ) {
                    Icon(
                        when (uiState.trackingMode) {
                            MapTrackingMode.FOLLOW -> Icons.Rounded.Search
                            MapTrackingMode.FREE -> Icons.Rounded.Close
                            MapTrackingMode.OVERVIEW -> Icons.Rounded.LocationOn
                        },
                        contentDescription = "Harita takip modu",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = {
                        if (uiState.isTracking) {
                            viewModel.stopTracking()
                        } else if (uiState.isLocationPermissionGranted) {
                            viewModel.startTracking()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    containerColor = if (uiState.isTracking) ErrorRed else SuccessGreen
                ) {
                    Icon(
                        if (uiState.isTracking) Icons.Rounded.Clear else Icons.Rounded.PlayArrow,
                        contentDescription = if (uiState.isTracking) {
                            "Konum takibini durdur"
                        } else {
                            "Konum takibini başlat"
                        },
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val safeMyLocationEnabled = uiState.isLocationPermissionGranted && !locationSecurityException
            val mapProperties = MapProperties(
                mapType = when (uiState.mapStyle) {
                    MapStyle.NORMAL -> MapType.NORMAL
                    MapStyle.SATELLITE -> MapType.SATELLITE
                    MapStyle.TERRAIN -> MapType.TERRAIN
                    MapStyle.HYBRID -> MapType.HYBRID
                },
                isMyLocationEnabled = false
            )
            val mapUiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            )

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                MapEffect(safeMyLocationEnabled) { map ->
                    try {
                        map.isMyLocationEnabled = safeMyLocationEnabled
                        if (locationSecurityException) {
                            locationSecurityException = false
                        }
                    } catch (se: SecurityException) {
                        locationSecurityException = true
                        Timber.w(se, "GoogleMap location layer requires location permission")
                    }
                }

                uiState.routePolyline?.let { polyline ->
                    Polyline(
                        points = polyline.points,
                        color = OzyuceBlue,
                        width = 8f,
                        jointType = JointType.ROUND
                    )
                }

                uiState.stopMarkers.forEach { stop ->
                    Marker(
                        state = MarkerState(stop.location),
                        title = stop.name,
                        snippet = "Sıra: ${stop.sequence} | Saat: ${stop.scheduledTime}",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            when {
                                stop.isCompleted -> BitmapDescriptorFactory.HUE_GREEN
                                stop == uiState.selectedStop -> BitmapDescriptorFactory.HUE_AZURE
                                else -> BitmapDescriptorFactory.HUE_RED
                            }
                        ),
                        onClick = {
                            viewModel.onStopSelected(stop)
                            true
                        }
                    )
                }

                uiState.vehicleLocation?.let { vehicle ->
                    Marker(
                        state = MarkerState(vehicle.location),
                        title = "Araç",
                        rotation = vehicle.heading,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )

                    if (uiState.trackingMode == MapTrackingMode.FOLLOW) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(vehicle.location)
                                    .zoom(15f)
                                    .bearing(vehicle.heading)
                                    .build()
                            )
                        )
                    }
                }
            }

            uiState.selectedStop?.let { stop ->
                StopDetailCard(
                    stop = stop,
                    eta = uiState.routeEta
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }

            if (!uiState.isLocationPermissionGranted || locationSecurityException) {
                PermissionRationaleCard(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.StopDetailCard(
    stop: StopMarker,
    eta: RouteEta?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.BottomCenter),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stop.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Planlanan: ${stop.scheduledTime}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Personel: ${stop.boardedCount}/${stop.personnelCount}",
                style = MaterialTheme.typography.bodyMedium
            )

            eta?.let { data ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tahmini Varış: ${SimpleDateFormat("HH:mm", Locale.forLanguageTag("tr-TR")).format(data.estimatedArrival)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Mesafe: %.1f km | Süre: %d dk".format(data.distance, data.duration),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PermissionRationaleCard(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "Konum izni gerekli",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aracınızın harita üzerinde takip edilebilmesi için konum izni vermelisiniz.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRequestPermission,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("İzin Ver")
            }
        }
    }
}




