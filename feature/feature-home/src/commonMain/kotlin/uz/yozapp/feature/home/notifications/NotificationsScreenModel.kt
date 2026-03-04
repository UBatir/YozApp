package uz.yozapp.feature.home.notifications

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class NotificationsScreenModel : MviScreenModel<NotificationsState, NotificationsIntent, NotificationsEffect>(
    initialState = NotificationsState()
) {

    override fun handleIntent(intent: NotificationsIntent) {
        when (intent) {
            NotificationsIntent.BackClicked    -> emitEffect(NotificationsEffect.NavigateBack)
            is NotificationsIntent.ItemClicked -> updateState { copy(selectedItem = intent.item) }
            NotificationsIntent.DismissSheet   -> updateState { copy(selectedItem = null) }
            NotificationsIntent.MarkAllRead    -> markAllRead()
        }
    }

    private fun markAllRead() {
        updateState {
            copy(
                notifications = notifications.map { it.copy(isRead = true) },
                snackbarVisible = true
            )
        }
        screenModelScope.launch {
            delay(3000)
            updateState { copy(snackbarVisible = false) }
        }
    }
}