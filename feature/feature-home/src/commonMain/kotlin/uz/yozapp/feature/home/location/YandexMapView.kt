package uz.yozapp.feature.home.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun YandexMapView(lat: Double, lon: Double, modifier: Modifier = Modifier)
