package uz.yozapp.feature.auth.name

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class NameScreenModel(
    private val phone: String,
    private val prefs: AppPreferences
) : MviScreenModel<State, Intent, Effect>(
    initialState = State()
) {

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.NameChanged -> updateState { copy(name = intent.name) }
            Intent.ContinueClicked -> onContinue()
            Intent.BackClicked -> emitEffect(Effect.NavigateBack)
        }
    }

    private fun onContinue() {
        if (!state.value.canContinue) return
        screenModelScope.launch {
            prefs.saveUser(name = state.value.name.trim(), phone = phone)
            emitEffect(Effect.NavigateToHome)
        }
    }
}
