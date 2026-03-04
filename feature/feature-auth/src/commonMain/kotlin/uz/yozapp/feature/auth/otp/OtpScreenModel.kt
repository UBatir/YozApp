package uz.yozapp.feature.auth.otp

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class OtpScreenModel(phone: String) : MviScreenModel<State, Intent, Effect>(
    initialState = State(phone = phone)
) {

    init {
        startTimer()
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.CodeChanged -> onCodeChanged(intent.code)
            Intent.VerifyClicked -> onVerify()
            Intent.ResendClicked -> onResend()
            Intent.BackClicked -> emitEffect(Effect.NavigateBack)
        }
    }

    private fun onCodeChanged(code: String) {
        if (code.length <= 5 && code.all { it.isDigit() }) {
            updateState { copy(code = code, error = null) }
            if (code.length == 5) onVerify()
        }
    }

    private fun onVerify() {
        if (!state.value.isCodeComplete) return
        updateState { copy(isLoading = true) }
        // TODO: вызов VerifyOtpUseCase
        emitEffect(Effect.NavigateToName(state.value.phone))
    }

    private fun onResend() {
        if (!state.value.canResend) return
        updateState { copy(code = "", error = null, canResend = false, timerSeconds = 60) }
        // TODO: вызов SendOtpUseCase
        startTimer()
    }

    private fun startTimer() {
        screenModelScope.launch {
            var seconds = 60
            while (seconds > 0) {
                delay(1000)
                seconds--
                updateState { copy(timerSeconds = seconds) }
            }
            updateState { copy(canResend = true) }
        }
    }
}
