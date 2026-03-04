package uz.yozapp.feature.home.favorites

import androidx.compose.ui.graphics.Color
import uz.yozapp.core.ui.theme.ColorSalonBg1
import uz.yozapp.core.ui.theme.ColorSalonBg2
import uz.yozapp.core.ui.theme.ColorSalonBg3

// ── Domain models ─────────────────────────────────────────────────────────────

internal enum class FavoriteFilter { All, Beauty, Clinics }

internal data class FavoriteSalon(
    val id: Int,
    val name: String,
    val address: String,
    val rating: Double,
    val reviewCount: Int,
    val imageBg: Color = ColorSalonBg1,
    val category: FavoriteFilter = FavoriteFilter.Beauty
)

// ── Mock data ─────────────────────────────────────────────────────────────────

internal val mockFavorites = listOf(
    FavoriteSalon(1, "Sadoqat Beauty Bar",  "ул. Бунёдкор 63",           4.7, 7, ColorSalonBg1, FavoriteFilter.Beauty),
    FavoriteSalon(2, "Noor Studio",         "проспект Мустакиллик, 14",  4.7, 7, ColorSalonBg2, FavoriteFilter.Beauty),
    FavoriteSalon(3, "SmileLine Clinic",    "ул. Абдуллы Кадыри, 45",   4.9, 7, ColorSalonBg3, FavoriteFilter.Clinics)
)

// ── MVI Contract ──────────────────────────────────────────────────────────────

internal data class FavoritesState(
    val salons: List<FavoriteSalon> = mockFavorites,
    val showFilterSheet: Boolean = false,
    val selectedFilter: FavoriteFilter = FavoriteFilter.All
) {
    val filteredSalons: List<FavoriteSalon> get() = when (selectedFilter) {
        FavoriteFilter.All -> salons
        else               -> salons.filter { it.category == selectedFilter }
    }
}

internal sealed interface FavoritesIntent {
    data object FilterClicked : FavoritesIntent
    data object DismissFilter : FavoritesIntent
    data class SelectFilter(val filter: FavoriteFilter) : FavoritesIntent
    data class RemoveFavorite(val salonId: Int) : FavoritesIntent
}

internal sealed interface FavoritesEffect