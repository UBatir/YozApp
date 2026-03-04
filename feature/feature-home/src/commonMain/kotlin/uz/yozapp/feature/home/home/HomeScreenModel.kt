package uz.yozapp.feature.home.home

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class HomeScreenModel(
    private val prefs: AppPreferences
) : MviScreenModel<HomeState, HomeIntent, HomeEffect>(
    initialState = HomeState()
) {

    init {
        screenModelScope.launch {
            prefs.userName.collect { name ->
                if (name != null) updateState { copy(userName = name) }
            }
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectTab         -> updateState { copy(selectedTabIndex = intent.index) }
            is HomeIntent.SelectCategory    -> updateState { copy(selectedCategoryIndex = intent.index) }
            HomeIntent.NotificationsClicked -> emitEffect(HomeEffect.NavigateToNotifications)
            HomeIntent.ProfileClicked       -> emitEffect(HomeEffect.NavigateToProfile)
            is HomeIntent.SeeAllClicked     -> emitEffect(
                HomeEffect.NavigateToSectionList(intent.title, intent.sectionIndex)
            )
        }
    }
}