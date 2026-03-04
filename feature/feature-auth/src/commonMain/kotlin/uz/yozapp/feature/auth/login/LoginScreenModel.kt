package uz.yozapp.feature.auth.login

import uz.yozapp.core.ui.mvi.MviScreenModel

internal class LoginScreenModel : MviScreenModel<State, Intent, Effect>(
    initialState = State()
) {

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.PhoneChanged -> onPhoneChanged(intent.phone)
            Intent.GetCodeClicked -> onGetCode()
            Intent.BackClicked -> emitEffect(Effect.NavigateBack)
        }
    }

    private fun onPhoneChanged(phone: String) {
        if (phone.length <= 9 && phone.all { it.isDigit() }) {
            updateState { copy(phoneNumber = phone, error = null) }
        }
    }

    private fun onGetCode() {
        if (!state.value.isPhoneComplete) return
        val phone = state.value.phoneNumber
        // TODO: вызов SendOtpUseCase
        emitEffect(Effect.NavigateToOtp(phone = "+998$phone"))
    }
}