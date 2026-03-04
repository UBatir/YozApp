package uz.yozapp.feature.home.bookings

// ── Domain models ─────────────────────────────────────────────────────────────

internal data class Specialist(
    val id: Int,
    val name: String
)

internal data class Booking(
    val id: Int,
    val dateTime: String,
    val salonName: String,
    val services: List<String>,
    val specialists: List<Specialist>,
    val address: String,
    val isAddedToCalendar: Boolean = false
)

internal data class HistoryItem(
    val id: Int,
    val services: String,
    val salonName: String,
    val date: String,
    val isCancelled: Boolean = false
)

internal data class HistoryGroup(
    val month: String,
    val items: List<HistoryItem>
)

internal data class ServiceItem(
    val name: String,
    val priceFrom: Int,
    val durationLabel: String
)

internal data class ServiceSpecialistAssignment(
    val serviceName: String,
    val specialist: Specialist?   // null = "Любой свободный специалист"
)

internal data class TimeSlot(
    val time: String,
    val isBusy: Boolean
)

internal enum class RescheduleStep { None, QuickDate, Calendar, TimePicker, Confirm }

// ── Mock data ─────────────────────────────────────────────────────────────────

internal val mockSpecialists = listOf(
    Specialist(1, "Zulfiya Nematova"),
    Specialist(2, "Dildora Karimova"),
    Specialist(3, "Dilrabo Ergasheva"),
    Specialist(4, "Malika Abdurahmonova")
)

internal val mockBookings = listOf(
    Booking(
        id = 1,
        dateTime = "16 июня, 15:00",
        salonName = "Sadoqat Beauty Bar",
        services = listOf("Снятие гель-лака", "Маникюр с гель-лаком", "Укрепление ногтей"),
        specialists = mockSpecialists.take(3),
        address = "ул. Бунёдкор 63"
    )
)

internal val mockHistory = listOf(
    HistoryGroup("Июнь", listOf(
        HistoryItem(1, "Маникюр, Гель-лак",         "Nails & Beauty",      "14 июня"),
        HistoryItem(2, "Стрижка, Укладка",           "Sadoqat Beauty Bar",  "12 июня"),
        HistoryItem(3, "Отбеливание зубов",          "WhiteSmile Clinic",   "4 июня")
    )),
    HistoryGroup("Май", listOf(
        HistoryItem(4, "Массаж спины",               "",                    "21 мая",    isCancelled = true),
        HistoryItem(5, "Педикюр",                    "Luna Studio",         "14 мая")
    )),
    HistoryGroup("Декабрь 2024", listOf(
        HistoryItem(6, "Стрижка, Уход за волосами",  "Sadoqat Beauty Bar",  "30 декабря"),
        HistoryItem(7, "Консультация стоматолога",   "SmileLine Clinic",    "27 декабря"),
        HistoryItem(8, "Общий массаж",               "Balance Spa",         "14 декабря")
    ))
)

internal val mockServices = listOf(
    ServiceItem("Оформление бровей",            60_000,  "1 ч"),
    ServiceItem("Маникюр с гель-лаком",        120_000, "30 м"),
    ServiceItem("Уход за лицом \"Silk Glow\"", 150_000,  "2 ч"),
    ServiceItem("Ламинирование ресниц",        180_000,  "1 ч"),
    ServiceItem("Парафинотерапия для рук",      50_000, "25 м"),
    ServiceItem("Дизайн ногтей (2 акцента)",   30_000, "15 м"),
    ServiceItem("Снятие гель-лака",             20_000, "20 м"),
    ServiceItem("Укрепление ногтей",            40_000, "30 м"),
    ServiceItem("SPA-уход для рук",             60_000, "25 м")
)

internal val mockTimeSlots = listOf(
    TimeSlot("9:00",  false),
    TimeSlot("10:00", false),
    TimeSlot("11:00", true),
    TimeSlot("12:00", false),
    TimeSlot("13:00", false),
    TimeSlot("14:00", true),
    TimeSlot("15:00", false),
    TimeSlot("16:00", false),
    TimeSlot("17:00", false),
    TimeSlot("18:00", false)
)

internal val mockQuickDates = listOf(
    "Завтра, 16 июня",
    "17 июня",
    "18 июня"
)

// ── Calendar helpers ───────────────────────────────────────────────────────────

internal val monthNamesRu = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
)

internal val monthNamesRuGenitive = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря"
)

internal fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11            -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 30
}

/** Returns 0=Mon … 6=Sun offset for the first day of the month (Russian week). */
internal fun firstDayOffset(year: Int, month: Int): Int {
    val m = if (month < 3) month + 12 else month
    val y = if (month < 3) year - 1 else year
    val k = y % 100
    val j = y / 100
    // Zeller's congruence → h: 0=Sat,1=Sun,2=Mon,3=Tue,4=Wed,5=Thu,6=Fri
    val h = (1 + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 - 2 * j).mod(7)
    return when (h) { 0 -> 5; 1 -> 6; 2 -> 0; 3 -> 1; 4 -> 2; 5 -> 3; 6 -> 4; else -> 0 }
}

// ── MVI Contract ──────────────────────────────────────────────────────────────

internal data class BookingsState(
    val selectedSubTab: Int = 0,
    val bookings: List<Booking> = mockBookings,
    val history: List<HistoryGroup> = mockHistory,
    val specialistsSheet: Booking? = null,
    val actionsSheet: Booking? = null,
    val snackbarVisible: Boolean = false,
    // Edit booking
    val editingBooking: Booking? = null,
    val editAssignments: List<ServiceSpecialistAssignment> = emptyList(),
    val showServicePicker: Boolean = false,
    val tempSelectedServices: Set<String> = emptySet(),
    // Reschedule flow
    val rescheduleStep: RescheduleStep = RescheduleStep.None,
    val rescheduleBooking: Booking? = null,
    val rescheduleDate: String? = null,
    val rescheduleTime: String? = null,
    val calendarYear: Int = 2025,
    val calendarMonth: Int = 6,
    val calendarSelectedDay: Int? = null,
    val rescheduleSnackbar: String? = null,
    // Cancel booking
    val showCancelConfirm: Boolean = false,
    val cancelBooking: Booking? = null,
    val cancelSnackbar: Boolean = false
)

internal sealed interface BookingsIntent {
    data class SelectSubTab(val index: Int) : BookingsIntent
    data class AddToCalendar(val bookingId: Int) : BookingsIntent
    data class ShowSpecialistsSheet(val booking: Booking) : BookingsIntent
    data class ShowActionsSheet(val booking: Booking) : BookingsIntent
    data object DismissSpecialistsSheet : BookingsIntent
    data object DismissActionsSheet : BookingsIntent
    data object RescheduleClicked : BookingsIntent
    data object ChangeClicked : BookingsIntent
    data object CancelClicked : BookingsIntent
    // Edit booking
    data object DismissEditBooking : BookingsIntent
    data object OpenServicePicker : BookingsIntent
    data object DismissServicePicker : BookingsIntent
    data class ToggleService(val serviceName: String) : BookingsIntent
    data object ConfirmServiceSelection : BookingsIntent
    data object SaveChanges : BookingsIntent
    // Reschedule flow
    data object DismissReschedule : BookingsIntent
    data class QuickDatePicked(val label: String) : BookingsIntent
    data object OpenCalendar : BookingsIntent
    data class CalendarDayPicked(val day: Int) : BookingsIntent
    data object CalendarPrevMonth : BookingsIntent
    data object CalendarNextMonth : BookingsIntent
    data class TimePicked(val time: String) : BookingsIntent
    data object ConfirmReschedule : BookingsIntent
    // Cancel booking
    data object ConfirmCancel : BookingsIntent
    data object DismissCancel : BookingsIntent
}

internal sealed interface BookingsEffect