package uz.yozapp.feature.auth.otp

internal data class State(
    val phone: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val timerSeconds: Int = 60,
    val canResend: Boolean = false
) {
    val isCodeComplete: Boolean get() = code.length == 5
}

internal sealed interface Intent {
    data class CodeChanged(val code: String) : Intent
    data object VerifyClicked : Intent
    data object ResendClicked : Intent
    data object BackClicked : Intent
}

internal sealed interface Effect {
    data class NavigateToName(val phone: String) : Effect
    data object NavigateBack : Effect
}
