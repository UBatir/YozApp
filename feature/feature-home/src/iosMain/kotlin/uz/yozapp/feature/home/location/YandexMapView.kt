package uz.yozapp.feature.home.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
actual fun YandexMapView(lat: Double, lon: Double, modifier: Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFE8EEE8)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map: $lat, $lon",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}
