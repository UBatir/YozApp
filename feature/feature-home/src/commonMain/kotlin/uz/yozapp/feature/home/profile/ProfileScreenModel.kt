package uz.yozapp.feature.home.profile

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.core.ui.mvi.MviScreenModel

private data class ProfileSnapshot(
    val isGuest: Boolean,
    val name: String?,
    val phone: String?
)

internal class ProfileScreenModel(
    private val prefs: AppPreferences
) : MviScreenModel<ProfileState, ProfileIntent, ProfileEffect>(
    initialState = ProfileState()
) {

    init {
        screenModelScope.launch {
            combine(prefs.isGuest, prefs.userName, prefs.userPhone) { isGuest, name, phone ->
                ProfileSnapshot(isGuest = isGuest, name = name, phone = phone)
            }.collect { snapshot ->
                updateState {
                    copy(
                        isLoggedIn = !snapshot.isGuest && snapshot.name != null,
                        user = UserProfile(
                            name = snapshot.name.orEmpty(),
                            phone = snapshot.phone.orEmpty()
                        )
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.BackClicked -> emitEffect(ProfileEffect.NavigateBack)
            ProfileIntent.LoginClicked -> emitEffect(ProfileEffect.NavigateToWelcome)
            ProfileIntent.EditClicked -> emitEffect(ProfileEffect.NavigateToEdit)
            ProfileIntent.LanguageRowClicked -> updateState { copy(showLanguagePicker = true) }
            is ProfileIntent.LanguageSelected -> updateState {
                copy(selectedLanguage = intent.language, showLanguagePicker = false)
            }

            ProfileIntent.DismissLanguagePicker -> updateState { copy(showLanguagePicker = false) }
            ProfileIntent.ToggleNotifications -> updateState { copy(notificationsEnabled = !notificationsEnabled) }
            ProfileIntent.LogoutClicked -> logout()
            ProfileIntent.DeleteAccountClicked -> logout()
            ProfileIntent.EmailClicked -> { /* TODO */
            }
        }
    }

    private fun logout() {
        screenModelScope.launch {
            prefs.logout()
            emitEffect(ProfileEffect.NavigateToWelcome)
        }
    }
}