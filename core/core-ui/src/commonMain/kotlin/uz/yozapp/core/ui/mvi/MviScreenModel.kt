package uz.yozapp.core.ui.mvi

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviScreenModel<S, I, E>(
    initialState: S
) : ScreenModel {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    abstract fun handleIntent(intent: I)

    protected fun updateState(update: S.() -> S) {
        _state.update { it.update() }
    }

    protected fun emitEffect(effect: E) {
        screenModelScope.launch {
            _effect.send(effect)
        }
    }
}
