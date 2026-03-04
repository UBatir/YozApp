package uz.yozapp.feature.home.home

internal data class HomeState(
    val userName: String = "Diyora",
    val hasNotification: Boolean = true,
    val selectedCategoryIndex: Int = 1,
    val selectedTabIndex: Int = 0
)

internal sealed interface HomeIntent {
    data class SelectTab(val index: Int) : HomeIntent
    data class SelectCategory(val index: Int) : HomeIntent
    data object NotificationsClicked : HomeIntent
    data object ProfileClicked : HomeIntent
    data class SeeAllClicked(val title: String, val sectionIndex: Int) : HomeIntent
}

internal sealed interface HomeEffect {
    data object NavigateToNotifications : HomeEffect
    data object NavigateToProfile : HomeEffect
    data class NavigateToSectionList(val title: String, val sectionIndex: Int) : HomeEffect
}