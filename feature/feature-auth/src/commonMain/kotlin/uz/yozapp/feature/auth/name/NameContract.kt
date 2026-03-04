package uz.yozapp.feature.auth.name

internal data class State(
    val name: String = "",
    val isLoading: Boolean = false
) {
    val canContinue: Boolean get() = name.trim().length >= 2
}

internal sealed interface Intent {
    data class NameChanged(val name: String) : Intent
    data object ContinueClicked : Intent
    data object BackClicked : Intent
}

internal sealed interface Effect {
    data object NavigateToHome : Effect
    data object NavigateBack : Effect
}
