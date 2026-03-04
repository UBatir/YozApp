package uz.yozapp.feature.home.notifications

// ── Domain models ─────────────────────────────────────────────────────────────

internal enum class NotificationType { REVIEW, REMINDER, RESCHEDULE }

internal data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean = false
)

internal val mockNotifications = listOf(
    NotificationItem(
        id = 1,
        type = NotificationType.REVIEW,
        title = "Как вам обслуживание?",
        body = "Оцените вашу запись в Sadoqat Beauty Bar",
        time = "10 мин назад",
        isRead = false
    ),
    NotificationItem(
        id = 2,
        type = NotificationType.REMINDER,
        title = "Напоминаем",
        body = "Завтра в 15:00 у вас запись в Sadoqat Beauty Bar",
        time = "Вчера в 14:30",
        isRead = true
    ),
    NotificationItem(
        id = 3,
        type = NotificationType.RESCHEDULE,
        title = "Перенос записи",
        body = "Ваша запись в Sadoqat Beauty Bar перенесена на 12 июня в 11:00",
        time = "2 дня назад",
        isRead = true
    )
)

// ── MVI Contract ──────────────────────────────────────────────────────────────

internal data class NotificationsState(
    val notifications: List<NotificationItem> = mockNotifications,
    val selectedItem: NotificationItem? = null,
    val snackbarVisible: Boolean = false
)

internal sealed interface NotificationsIntent {
    data object BackClicked : NotificationsIntent
    data class ItemClicked(val item: NotificationItem) : NotificationsIntent
    data object DismissSheet : NotificationsIntent
    data object MarkAllRead : NotificationsIntent
}

internal sealed interface NotificationsEffect {
    data object NavigateBack : NotificationsEffect
}