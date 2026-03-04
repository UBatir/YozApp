package uz.yozapp.feature.home.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.stringResource
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgInputEmpty
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextPrimary

// ── Quick date picker dialog ───────────────────────────────────────────────────

@Composable
internal fun QuickDateDialog(
    onDateSelected: (String) -> Unit,
    onOpenCalendar: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
        ) {
            Column {
                mockQuickDates.forEachIndexed { index, date ->
                    Text(
                        text = date,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDateSelected(date) }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        fontSize = 16.sp,
                        color = ColorTextPrimary
                    )
                    HorizontalDivider(color = ColorDivider)
                }
                Text(
                    text = stringResource(Res.string.reschedule_pick_other_day),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onOpenCalendar() }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    fontSize = 16.sp,
                    color = ColorTextGray
                )
            }
        }
    }
}

// ── Calendar picker dialog ─────────────────────────────────────────────────────

private val weekDayLabels = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")

@Composable
internal fun CalendarDialog(
    year: Int,
    month: Int,
    selectedDay: Int?,
    todayDay: Int = 16,
    onDaySelected: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDismiss: () -> Unit
) {
    val days   = daysInMonth(year, month)
    val offset = firstDayOffset(year, month)
    val total  = offset + days
    val rows   = (total + 6) / 7

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${monthNamesRu[month - 1]} $year",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onPrevMonth, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = ColorTextPrimary
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ColorTextPrimary
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Week day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDayLabels.forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = ColorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Calendar grid
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - offset + 1
                            if (day < 1 || day > days) {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                CalendarDayCell(
                                    day        = day,
                                    isToday    = day == todayDay,
                                    isSelected = day == selectedDay,
                                    modifier   = Modifier.weight(1f),
                                    onClick    = { onDaySelected(day) }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> ColorPrimary
                    else       -> Color.Transparent
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .then(
                        Modifier.padding(2.dp)
                    )
            )
            // Outline circle for today
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorPrimary
                )
            }
        } else {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    else       -> ColorTextPrimary
                }
            )
        }
    }
}

// ── Time picker dialog ─────────────────────────────────────────────────────────

@Composable
internal fun TimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val busyLabel = stringResource(Res.string.reschedule_slot_busy)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
        ) {
            LazyColumn {
                items(mockTimeSlots) { slot ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = !slot.isBusy,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTimeSelected(slot.time) }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = slot.time,
                            fontSize = 16.sp,
                            color = if (slot.isBusy) ColorTextHint else ColorTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (slot.isBusy) {
                            Text(
                                text = busyLabel,
                                fontSize = 13.sp,
                                color = ColorTextHint
                            )
                        }
                    }
                    HorizontalDivider(color = ColorDivider)
                }
            }
        }
    }
}

// ── Reschedule confirm dialog ──────────────────────────────────────────────────

@Composable
internal fun RescheduleConfirmDialog(
    date: String,
    time: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = stringResource(Res.string.reschedule_confirm_message, "$date, $time")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = stringResource(Res.string.reschedule_confirm_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = ColorTextGray,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.reschedule_btn_cancel),
                            fontSize = 15.sp,
                            color = ColorTextPrimary
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                    ) {
                        Text(
                            text = stringResource(Res.string.reschedule_btn_confirm),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ── Cancel booking confirm dialog ─────────────────────────────────────────────

@Composable
internal fun CancelBookingDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                Text(
                    text = stringResource(Res.string.cancel_booking_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.cancel_booking_message),
                    fontSize = 14.sp,
                    color = ColorTextGray,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorBgInputEmpty)
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel_booking_btn_no),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextPrimary
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel_booking_btn_confirm),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}