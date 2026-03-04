package uz.yozapp.feature.auth.welcome

internal data class State(
    val isCheckingAuth: Boolean = true,
    val isLoading: Boolean = false
)

internal sealed interface Intent {
    data object LoginClicked : Intent
    data object GoogleClicked : Intent
    data object GuestClicked : Intent
}

internal sealed interface Effect {
    data object NavigateToLogin : Effect
    data object NavigateToHome : Effect
}
