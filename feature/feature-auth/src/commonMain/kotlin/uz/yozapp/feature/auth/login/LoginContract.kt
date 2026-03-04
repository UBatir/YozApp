package uz.yozapp.feature.auth.login

internal data class State(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isPhoneComplete: Boolean get() = phoneNumber.length == 9
}

internal sealed interface Intent {
    data class PhoneChanged(val phone: String) : Intent
    data object GetCodeClicked : Intent
    data object BackClicked : Intent
}

internal sealed interface Effect {
    data class NavigateToOtp(val phone: String) : Effect
    data object NavigateBack : Effect
}
