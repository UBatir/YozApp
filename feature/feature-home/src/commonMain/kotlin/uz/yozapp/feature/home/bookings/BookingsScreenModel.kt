package uz.yozapp.feature.home.bookings

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yozapp.core.ui.mvi.MviScreenModel

internal class BookingsScreenModel : MviScreenModel<BookingsState, BookingsIntent, BookingsEffect>(
    initialState = BookingsState()
) {

    override fun handleIntent(intent: BookingsIntent) {
        when (intent) {
            is BookingsIntent.SelectSubTab         -> updateState { copy(selectedSubTab = intent.index) }
            is BookingsIntent.AddToCalendar        -> addToCalendar(intent.bookingId)
            is BookingsIntent.ShowSpecialistsSheet -> updateState { copy(specialistsSheet = intent.booking) }
            is BookingsIntent.ShowActionsSheet     -> updateState { copy(actionsSheet = intent.booking) }
            BookingsIntent.DismissSpecialistsSheet -> updateState { copy(specialistsSheet = null) }
            BookingsIntent.DismissActionsSheet     -> updateState { copy(actionsSheet = null) }
            BookingsIntent.RescheduleClicked       -> openReschedule()
            BookingsIntent.ChangeClicked           -> openEditBooking()
            BookingsIntent.CancelClicked           -> updateState {
                copy(actionsSheet = null, showCancelConfirm = true, cancelBooking = actionsSheet)
            }
            // Edit booking
            BookingsIntent.DismissEditBooking      -> updateState {
                copy(editingBooking = null, editAssignments = emptyList(), showServicePicker = false)
            }
            BookingsIntent.OpenServicePicker       -> updateState {
                copy(
                    showServicePicker = true,
                    tempSelectedServices = editAssignments.map { it.serviceName }.toSet()
                )
            }
            BookingsIntent.DismissServicePicker    -> updateState { copy(showServicePicker = false) }
            is BookingsIntent.ToggleService        -> toggleService(intent.serviceName)
            BookingsIntent.ConfirmServiceSelection -> confirmServiceSelection()
            BookingsIntent.SaveChanges             -> saveChanges()
            // Reschedule
            BookingsIntent.DismissReschedule       -> updateState {
                copy(
                    rescheduleStep = RescheduleStep.None,
                    rescheduleBooking = null,
                    rescheduleDate = null,
                    rescheduleTime = null,
                    calendarSelectedDay = null
                )
            }
            is BookingsIntent.QuickDatePicked      -> updateState {
                copy(rescheduleStep = RescheduleStep.TimePicker, rescheduleDate = intent.label)
            }
            BookingsIntent.OpenCalendar            -> updateState { copy(rescheduleStep = RescheduleStep.Calendar) }
            is BookingsIntent.CalendarDayPicked    -> {
                val label = "${intent.day} ${monthNamesRuGenitive[state.value.calendarMonth - 1]}"
                updateState {
                    copy(
                        rescheduleStep = RescheduleStep.TimePicker,
                        calendarSelectedDay = intent.day,
                        rescheduleDate = label
                    )
                }
            }
            BookingsIntent.CalendarPrevMonth       -> updateState {
                val m = if (calendarMonth == 1) 12 else calendarMonth - 1
                val y = if (calendarMonth == 1) calendarYear - 1 else calendarYear
                copy(calendarMonth = m, calendarYear = y, calendarSelectedDay = null)
            }
            BookingsIntent.CalendarNextMonth       -> updateState {
                val m = if (calendarMonth == 12) 1 else calendarMonth + 1
                val y = if (calendarMonth == 12) calendarYear + 1 else calendarYear
                copy(calendarMonth = m, calendarYear = y, calendarSelectedDay = null)
            }
            is BookingsIntent.TimePicked           -> updateState {
                copy(rescheduleStep = RescheduleStep.Confirm, rescheduleTime = intent.time)
            }
            BookingsIntent.ConfirmReschedule       -> confirmReschedule()
            BookingsIntent.ConfirmCancel           -> confirmCancel()
            BookingsIntent.DismissCancel           -> updateState {
                copy(showCancelConfirm = false, cancelBooking = null)
            }
        }
    }

    // ── Add to calendar ────────────────────────────────────────────────────────

    private fun addToCalendar(bookingId: Int) {
        updateState {
            copy(
                bookings = bookings.map {
                    if (it.id == bookingId) it.copy(isAddedToCalendar = true) else it
                },
                snackbarVisible = true
            )
        }
        screenModelScope.launch {
            delay(2500)
            updateState { copy(snackbarVisible = false) }
        }
    }

    // ── Edit booking ───────────────────────────────────────────────────────────

    private fun openEditBooking() {
        val booking = state.value.actionsSheet ?: return
        val assignments = booking.services.mapIndexed { i, service ->
            ServiceSpecialistAssignment(
                serviceName = service,
                specialist  = booking.specialists.getOrNull(i)
            )
        }
        updateState {
            copy(
                actionsSheet    = null,
                editingBooking  = booking,
                editAssignments = assignments
            )
        }
    }

    private fun toggleService(serviceName: String) {
        val current = state.value.tempSelectedServices
        updateState {
            copy(
                tempSelectedServices = if (serviceName in current) current - serviceName
                                       else current + serviceName
            )
        }
    }

    private fun confirmServiceSelection() {
        val current   = state.value
        val newNames  = current.tempSelectedServices
        val kept      = current.editAssignments.filter { it.serviceName in newNames }
        val keptNames = kept.map { it.serviceName }.toSet()
        val added     = (newNames - keptNames).map { ServiceSpecialistAssignment(it, null) }
        updateState {
            copy(editAssignments = kept + added, showServicePicker = false)
        }
    }

    private fun saveChanges() {
        val current = state.value
        val booking = current.editingBooking ?: return
        val updated = booking.copy(
            services    = current.editAssignments.map { it.serviceName },
            specialists = current.editAssignments.mapNotNull { it.specialist }
        )
        updateState {
            copy(
                bookings        = bookings.map { if (it.id == updated.id) updated else it },
                editingBooking  = null,
                editAssignments = emptyList(),
                showServicePicker = false,
                snackbarVisible = true
            )
        }
        screenModelScope.launch {
            delay(2500)
            updateState { copy(snackbarVisible = false) }
        }
    }

    // ── Reschedule ─────────────────────────────────────────────────────────────

    private fun openReschedule() {
        val booking = state.value.actionsSheet ?: return
        updateState {
            copy(
                actionsSheet  = null,
                rescheduleStep = RescheduleStep.QuickDate,
                rescheduleBooking = booking
            )
        }
    }

    private fun confirmCancel() {
        val booking = state.value.cancelBooking ?: return
        updateState {
            copy(
                bookings          = bookings.filter { it.id != booking.id },
                showCancelConfirm = false,
                cancelBooking     = null,
                cancelSnackbar    = true
            )
        }
        screenModelScope.launch {
            delay(3000)
            updateState { copy(cancelSnackbar = false) }
        }
    }

    private fun confirmReschedule() {
        val current = state.value
        val booking = current.rescheduleBooking ?: return
        val newDateTime = "${current.rescheduleDate}, ${current.rescheduleTime}"
        val updated = booking.copy(dateTime = newDateTime)
        updateState {
            copy(
                bookings          = bookings.map { if (it.id == updated.id) updated else it },
                rescheduleStep    = RescheduleStep.None,
                rescheduleBooking = null,
                rescheduleDate    = null,
                rescheduleTime    = null,
                calendarSelectedDay = null,
                rescheduleSnackbar = "Запись перенесена на $newDateTime"
            )
        }
        screenModelScope.launch {
            delay(3000)
            updateState { copy(rescheduleSnackbar = null) }
        }
    }
}