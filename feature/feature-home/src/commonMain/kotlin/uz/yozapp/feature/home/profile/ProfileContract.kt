package uz.yozapp.feature.home.profile

// ── Domain models ─────────────────────────────────────────────────────────────

internal data class UserProfile(
    val name: String,
    val phone: String,
    val gender: String? = null,
    val birthDate: String? = null,
    val email: String? = null
)

internal enum class AppLanguage { RUSSIAN, UZBEK, ENGLISH }

// ── MVI Contract ──────────────────────────────────────────────────────────────

internal data class ProfileState(
    val isLoggedIn: Boolean = false,
    val user: UserProfile = UserProfile(name = "", phone = ""),
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: AppLanguage = AppLanguage.RUSSIAN,
    val showLanguagePicker: Boolean = false
)

internal sealed interface ProfileIntent {
    data object BackClicked           : ProfileIntent
    data object LoginClicked          : ProfileIntent
    data object EditClicked           : ProfileIntent
    data object LanguageRowClicked    : ProfileIntent
    data class  LanguageSelected(val language: AppLanguage) : ProfileIntent
    data object DismissLanguagePicker : ProfileIntent
    data object ToggleNotifications   : ProfileIntent
    data object LogoutClicked         : ProfileIntent
    data object DeleteAccountClicked  : ProfileIntent
    data object EmailClicked          : ProfileIntent
}

internal sealed interface ProfileEffect {
    data object NavigateBack      : ProfileEffect
    data object NavigateToWelcome : ProfileEffect
    data object NavigateToEdit    : ProfileEffect
}