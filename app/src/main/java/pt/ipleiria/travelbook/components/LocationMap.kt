package pt.ipleiria.travelbook.components

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun LocationMap(locationName: String, country: String = "", onMapLoaded: (() -> Unit)? = null) {
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context) }

    var isSearching by remember { mutableStateOf(false) }
    var notFound by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    var isMapLoading by remember { mutableStateOf(true) }

    LaunchedEffect(locationName, country) {
        if (locationName.isBlank()) {
            notFound = false
            isSearching = false
            return@LaunchedEffect
        }

        if (!Geocoder.isPresent()) {
            notFound = true
            isSearching = false
            return@LaunchedEffect
        }

        isSearching = true
        notFound = false

        try {
            val addresses = geocoder.getFromLocationName("$locationName, $country", 1)
            if (addresses.isNullOrEmpty()) {
                notFound = true
            } else {
                val latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                markerState.position = latLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 10f)
                notFound = false
            }
        } catch (e: Exception) {
            notFound = true
        } finally {
            isSearching = false
        }
    }

    Box(modifier = Modifier.height(300.dp)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoading = false}
        ) {
            if (!notFound) {
                Marker(
                    state = markerState,
                    title = locationName,
                    snippet = "Marker in $locationName"
                )
            }
        }

        LoadingOverlay(isLoading = isSearching || isMapLoading)

        if (notFound && !isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No results found for\n$locationName $country",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}