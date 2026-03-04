package uz.yozapp.feature.home.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
actual fun YandexMapView(lat: Double, lon: Double, modifier: Modifier) {
    val context  = LocalContext.current
    val mapView  = remember { MapView(context) }

    DisposableEffect(Unit) {
        mapView.onStart()
        onDispose { mapView.onStop() }
    }

    AndroidView(
        factory = {
            mapView.apply {
                mapWindow.map.move(
                    CameraPosition(Point(lat, lon), 16f, 0f, 0f)
                )
            }
        },
        update = { view ->
            view.mapWindow.map.move(
                CameraPosition(Point(lat, lon), 16f, 0f, 0f)
            )
        },
        modifier = modifier
    )
}
