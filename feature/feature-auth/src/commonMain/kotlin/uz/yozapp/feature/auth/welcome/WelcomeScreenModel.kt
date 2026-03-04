package uz.yozapp.feature.auth.welcome

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class WelcomeScreenModel(
    private val prefs: AppPreferences
) : MviScreenModel<State, Intent, Effect>(
    initialState = State()
) {

    init {
        screenModelScope.launch {
            val isGuest = prefs.isGuest.first()
            val name = prefs.userName.first()
            if (!isGuest && name != null) {
                emitEffect(Effect.NavigateToHome)
            } else {
                updateState { copy(isCheckingAuth = false) }
            }
        }
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            Intent.LoginClicked -> emitEffect(Effect.NavigateToLogin)
            Intent.GoogleClicked -> { /* TODO: Google OAuth */
            }

            Intent.GuestClicked -> continueAsGuest()
        }
    }

    private fun continueAsGuest() {
        screenModelScope.launch {
            prefs.continueAsGuest()
            emitEffect(Effect.NavigateToHome)
        }
    }
}
