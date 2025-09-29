package com.ozyuce.maps.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.events.UiEvent
import com.ozyuce.maps.feature.map.components.LocationPermission
import com.ozyuce.maps.feature.map.components.StopsBottomSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigate: (String) -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val latestNavigate = rememberUpdatedState(onNavigate)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Navigate -> latestNavigate.value(event.route)
                else -> Unit
            }
        }
    }

    LocationPermission(
        onPermissionGranted = {
            hasLocationPermission = true
            showBottomSheet = true
        },
        modifier = Modifier.fillMaxSize()
    )

    if (!hasLocationPermission) {
        return
    }

    val mapProperties = remember(uiState.isDarkStyle, hasLocationPermission) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapStyleOptions = if (uiState.isDarkStyle) MapStyleOptions(DARK_MAP_STYLE_JSON) else null
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true,
            compassEnabled = true,
            rotationGesturesEnabled = true
        )
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Harita",
                onProfileClick = onProfileClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(MapEvent.ToggleMapStyle) }
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkStyle) {
                                Icons.Rounded.LightMode
                            } else {
                                Icons.Rounded.DarkMode
                            },
                            contentDescription = "Harita stilini değiştir"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        uiState.vehicleLocation,
                        15f
                    )
                },
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                Marker(
                    state = MarkerState(position = uiState.vehicleLocation),
                    title = "Araç Konumu",
                    snippet = "Şu anki konum"
                )

                Polyline(
                    points = uiState.routePolyline,
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )

                uiState.stops.forEach { stop ->
                    val markerHue = when (stop.status) {
                        StopStatus.COMPLETED -> BitmapDescriptorFactory.HUE_GREEN
                        StopStatus.NEXT -> BitmapDescriptorFactory.HUE_AZURE
                        StopStatus.PENDING -> BitmapDescriptorFactory.HUE_YELLOW
                    }

                    Marker(
                        state = MarkerState(position = stop.location),
                        title = stop.name,
                        snippet = "ETA: ",
                        icon = BitmapDescriptorFactory.defaultMarker(markerHue)
                    )
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState
                ) {
                    StopsBottomSheet(
                        stops = uiState.nextStops,
                        onStopClick = { stopId ->
                            viewModel.onEvent(MapEvent.NavigateToStop(stopId))
                        }
                    )
                }
            }
        }
    }
}

private const val DARK_MAP_STYLE_JSON = """
[
  {
    "elementType": "geometry",
    "stylers": [{ "color": "#0d1b2a" }]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#e0e0e0" }]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#0a1622" }]
  },
  {
    "featureType": "poi",
    "stylers": [{ "visibility": "off" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{ "color": "#1b263b" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#0d1b2a" }]
  },
  {
    "featureType": "transit",
    "stylers": [{ "visibility": "off" }]
  },
  {
    "featureType": "water",
    "stylers": [{ "color": "#132238" }]
  }
]
"""
