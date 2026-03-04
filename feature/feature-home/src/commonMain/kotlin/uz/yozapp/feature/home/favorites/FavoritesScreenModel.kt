package uz.yozapp.feature.home.favorites

import uz.yozapp.core.ui.mvi.MviScreenModel

internal class FavoritesScreenModel : MviScreenModel<FavoritesState, FavoritesIntent, FavoritesEffect>(
    initialState = FavoritesState()
) {

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            FavoritesIntent.FilterClicked      -> updateState { copy(showFilterSheet = true) }
            FavoritesIntent.DismissFilter      -> updateState { copy(showFilterSheet = false) }
            is FavoritesIntent.SelectFilter    -> updateState {
                copy(selectedFilter = intent.filter, showFilterSheet = false)
            }
            is FavoritesIntent.RemoveFavorite  -> updateState {
                copy(salons = salons.filter { it.id != intent.salonId })
            }
        }
    }
}