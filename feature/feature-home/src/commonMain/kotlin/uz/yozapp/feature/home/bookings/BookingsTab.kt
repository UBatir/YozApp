package uz.yozapp.feature.home.bookings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.stringResource
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgInputEmpty
import uz.yozapp.core.ui.theme.ColorBgLight
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorBorderMedium
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorError
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary

// ── Avatar background colors ──────────────────────────────────────────────────

private val avatarBgColors = listOf(
    Color(0xFFD8E8F0),
    Color(0xFFE8DDD0),
    Color(0xFFE8EEE8),
    Color(0xFFEDD8D8)
)

// ── Main composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun BookingsTab(
    state: BookingsState,
    onIntent: (BookingsIntent) -> Unit,
    onAddressClick: (address: String) -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxSize().background(ColorBgWhite)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BookingsSubTabSwitcher(
                selectedIndex = state.selectedSubTab,
                onSelect = { onIntent(BookingsIntent.SelectSubTab(it)) }
            )

            when (state.selectedSubTab) {
                0 -> if (state.bookings.isEmpty()) {
                    BookingsEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.bookings) { booking ->
                            BookingCard(
                                booking = booking,
                                onAddToCalendar = { onIntent(BookingsIntent.AddToCalendar(booking.id)) },
                                onMoreClick = { onIntent(BookingsIntent.ShowActionsSheet(booking)) },
                                onSpecialistsClick = { onIntent(BookingsIntent.ShowSpecialistsSheet(booking)) },
                                onAddressClick = { onAddressClick(booking.address) }
                            )
                        }
                    }
                }
                else -> if (state.history.isEmpty()) {
                    HistoryEmptyState()
                } else {
                    HistoryList(groups = state.history)
                }
            }
        }

        // Calendar snackbar
        AnimatedVisibility(
            visible = state.snackbarVisible,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D1D1D), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(Res.string.bookings_snackbar),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Reschedule snackbar
        AnimatedVisibility(
            visible = state.rescheduleSnackbar != null,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D1D1D), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = state.rescheduleSnackbar ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Cancel snackbar
        AnimatedVisibility(
            visible = state.cancelSnackbar,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D1D1D), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(Res.string.cancel_booking_snackbar),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Specialists bottom sheet
    state.specialistsSheet?.let { booking ->
        ModalBottomSheet(
            onDismissRequest = { onIntent(BookingsIntent.DismissSpecialistsSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorBgWhite
        ) {
            SpecialistsBottomSheet(specialists = booking.specialists)
        }
    }

    // Actions bottom sheet
    if (state.actionsSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(BookingsIntent.DismissActionsSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorBgWhite
        ) {
            ActionsBottomSheet(
                onReschedule = { onIntent(BookingsIntent.RescheduleClicked) },
                onChange     = { onIntent(BookingsIntent.ChangeClicked) },
                onCancel     = { onIntent(BookingsIntent.CancelClicked) }
            )
        }
    }

    // Edit booking sheet
    if (state.editingBooking != null) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(BookingsIntent.DismissEditBooking) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorBgWhite,
            modifier = Modifier.fillMaxSize()
        ) {
            EditBookingContent(
                assignments        = state.editAssignments,
                onOpenServicePicker = { onIntent(BookingsIntent.OpenServicePicker) },
                onSave             = { onIntent(BookingsIntent.SaveChanges) },
                onDismiss          = { onIntent(BookingsIntent.DismissEditBooking) }
            )
        }
    }

    // Service picker sheet
    if (state.showServicePicker) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(BookingsIntent.DismissServicePicker) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorBgWhite,
            modifier = Modifier.fillMaxSize()
        ) {
            ServicePickerContent(
                selectedServices = state.tempSelectedServices,
                onToggle         = { onIntent(BookingsIntent.ToggleService(it)) },
                onDone           = { onIntent(BookingsIntent.ConfirmServiceSelection) }
            )
        }
    }

    // Reschedule dialogs
    when (state.rescheduleStep) {
        RescheduleStep.QuickDate -> QuickDateDialog(
            onDateSelected = { onIntent(BookingsIntent.QuickDatePicked(it)) },
            onOpenCalendar = { onIntent(BookingsIntent.OpenCalendar) },
            onDismiss      = { onIntent(BookingsIntent.DismissReschedule) }
        )
        RescheduleStep.Calendar -> CalendarDialog(
            year         = state.calendarYear,
            month        = state.calendarMonth,
            selectedDay  = state.calendarSelectedDay,
            onDaySelected = { onIntent(BookingsIntent.CalendarDayPicked(it)) },
            onPrevMonth  = { onIntent(BookingsIntent.CalendarPrevMonth) },
            onNextMonth  = { onIntent(BookingsIntent.CalendarNextMonth) },
            onDismiss    = { onIntent(BookingsIntent.DismissReschedule) }
        )
        RescheduleStep.TimePicker -> TimePickerDialog(
            onTimeSelected = { onIntent(BookingsIntent.TimePicked(it)) },
            onDismiss      = { onIntent(BookingsIntent.DismissReschedule) }
        )
        RescheduleStep.Confirm -> RescheduleConfirmDialog(
            date      = state.rescheduleDate ?: "",
            time      = state.rescheduleTime ?: "",
            onConfirm = { onIntent(BookingsIntent.ConfirmReschedule) },
            onDismiss = { onIntent(BookingsIntent.DismissReschedule) }
        )
        RescheduleStep.None -> Unit
    }

    // Cancel booking confirm dialog
    if (state.showCancelConfirm) {
        CancelBookingDialog(
            onConfirm = { onIntent(BookingsIntent.ConfirmCancel) },
            onDismiss = { onIntent(BookingsIntent.DismissCancel) }
        )
    }
}

// ── Sub-tab switcher ──────────────────────────────────────────────────────────

@Composable
private fun BookingsSubTabSwitcher(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf(
        stringResource(Res.string.bookings_tab_upcoming),
        stringResource(Res.string.bookings_tab_history)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(40.dp)
            .background(ColorBgLight, RoundedCornerShape(10.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(3.dp)
                        .background(
                            if (selected) ColorPrimary else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) Color.White else ColorTextGray
                    )
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun BookingsEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.EditCalendar,
                contentDescription = null,
                tint = ColorTextHint,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.bookings_empty),
                fontSize = 15.sp,
                color = ColorTextGray
            )
        }
    }
}

// ── Booking card ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookingCard(
    booking: Booking,
    onAddToCalendar: () -> Unit,
    onMoreClick: () -> Unit,
    onSpecialistsClick: () -> Unit,
    onAddressClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = ColorBgWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.dateTime,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )
                IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = ColorTextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = booking.salonName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPrimary
            )

            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                booking.services.forEach { service -> ServiceChip(label = service) }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSpecialistsClick
                    )
            ) {
                Text(
                    text = stringResource(Res.string.bookings_specialist_label),
                    fontSize = 12.sp,
                    color = ColorTextGray
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OverlappingAvatars(specialists = booking.specialists)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = booking.specialists.joinToString(", ") { it.name },
                        fontSize = 14.sp,
                        color = ColorTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = ColorDivider)
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAddressClick
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.bookings_address_label),
                        fontSize = 12.sp,
                        color = ColorTextGray
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = booking.address,
                        fontSize = 14.sp,
                        color = ColorTextPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ColorTextGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            if (booking.isAddedToCalendar) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorderMedium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.bookings_already_in_calendar),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextHint
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ColorTextHint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onAddToCalendar,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorPrimary)
                ) {
                    Text(
                        text = stringResource(Res.string.bookings_add_to_calendar),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorPrimary
                    )
                }
            }
        }
    }
}

// ── Service chip ──────────────────────────────────────────────────────────────

@Composable
private fun ServiceChip(label: String) {
    Box(
        modifier = Modifier
            .background(ColorBgInputEmpty, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = ColorTextPrimary)
    }
}

// ── Overlapping specialist avatars ────────────────────────────────────────────

@Composable
private fun OverlappingAvatars(specialists: List<Specialist>) {
    val avatarSize = 28.dp
    val step       = 20.dp
    val maxShown   = minOf(specialists.size, 3)
    val totalWidth = avatarSize + (maxShown - 1) * step

    Box(modifier = Modifier.size(width = totalWidth, height = avatarSize)) {
        for (i in (maxShown - 1) downTo 0) {
            Box(
                modifier = Modifier
                    .offset(x = step * i)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(avatarBgColors[i % avatarBgColors.size])
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = specialists[i].name.firstOrNull()?.uppercase() ?: "",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextSecondary
                )
            }
        }
    }
}

// ── Specialists bottom sheet ──────────────────────────────────────────────────

@Composable
private fun SpecialistsBottomSheet(specialists: List<Specialist>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        specialists.forEach { specialist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarBgColors[specialist.id % avatarBgColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = specialist.name.firstOrNull()?.uppercase() ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextSecondary
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(text = specialist.name, fontSize = 15.sp, color = ColorTextPrimary)
            }
        }
    }
}

// ── Actions bottom sheet ──────────────────────────────────────────────────────

@Composable
private fun ActionsBottomSheet(onReschedule: () -> Unit, onChange: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        ActionItem(stringResource(Res.string.bookings_action_reschedule), ColorTextPrimary, onReschedule)
        HorizontalDivider(color = ColorDivider)
        ActionItem(stringResource(Res.string.bookings_action_change), ColorTextPrimary, onChange)
        HorizontalDivider(color = ColorDivider)
        ActionItem(stringResource(Res.string.bookings_action_cancel), ColorError, onCancel)
    }
}

@Composable
private fun ActionItem(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(text = label, fontSize = 16.sp, color = color)
    }
}

// ── History empty state ───────────────────────────────────────────────────────

@Composable
private fun HistoryEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                tint = ColorTextHint,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.history_empty),
                fontSize = 15.sp,
                color = ColorTextGray
            )
        }
    }
}

// ── History list ──────────────────────────────────────────────────────────────

@Composable
private fun HistoryList(groups: List<HistoryGroup>) {
    val cancelledLabel = stringResource(Res.string.history_cancelled)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        groups.forEach { group ->
            item { HistoryMonthHeader(month = group.month) }
            group.items.forEachIndexed { index, item ->
                item {
                    HistoryRow(item = item, cancelledLabel = cancelledLabel)
                    if (index < group.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = ColorDivider
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun HistoryMonthHeader(month: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = month, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ColorTextGray)
    }
}

@Composable
private fun HistoryRow(item: HistoryItem, cancelledLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.services,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextPrimary
            )
            Spacer(Modifier.height(3.dp))
            if (item.isCancelled) {
                Text(text = cancelledLabel, fontSize = 13.sp, color = ColorError)
            } else {
                Text(text = item.salonName, fontSize = 13.sp, color = ColorTextGray)
            }
        }
        Text(text = item.date, fontSize = 13.sp, color = ColorTextGray)
    }
}