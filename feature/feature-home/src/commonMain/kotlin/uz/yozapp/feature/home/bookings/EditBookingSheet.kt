package uz.yozapp.feature.home.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import uz.yozapp.core.ui.theme.ColorTextSecondary

// ── Edit booking content ───────────────────────────────────────────────────────

@Composable
internal fun EditBookingContent(
    assignments: List<ServiceSpecialistAssignment>,
    onOpenServicePicker: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ColorTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.edit_booking_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary
            )
        }

        HorizontalDivider(color = ColorDivider)

        // Scrollable body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Services section
            Text(
                text = stringResource(Res.string.edit_services_label),
                fontSize = 13.sp,
                color = ColorTextGray
            )
            Spacer(Modifier.height(8.dp))
            ServicesDropdownRow(
                assignments = assignments,
                onClick = onOpenServicePicker
            )

            Spacer(Modifier.height(24.dp))

            // Specialist section
            Text(
                text = stringResource(Res.string.edit_specialist_label),
                fontSize = 13.sp,
                color = ColorTextGray
            )
            Spacer(Modifier.height(8.dp))

            assignments.forEach { assignment ->
                SpecialistDropdownRow(assignment = assignment)
                Spacer(Modifier.height(6.dp))
                EditServiceChip(label = assignment.serviceName)
                Spacer(Modifier.height(12.dp))
            }
        }

        // Save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text(
                    text = stringResource(Res.string.edit_save_btn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Services dropdown row ──────────────────────────────────────────────────────

@Composable
private fun ServicesDropdownRow(
    assignments: List<ServiceSpecialistAssignment>,
    onClick: () -> Unit
) {
    val summary = assignments.joinToString(", ") { it.serviceName }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(ColorBgInputEmpty, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = summary,
                fontSize = 15.sp,
                color = ColorTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ColorTextGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Specialist dropdown row ───────────────────────────────────────────────────

private val avatarBgColors = listOf(
    Color(0xFFD8E8F0),
    Color(0xFFE8DDD0),
    Color(0xFFE8EEE8),
    Color(0xFFEDD8D8)
)

@Composable
private fun SpecialistDropdownRow(assignment: ServiceSpecialistAssignment) {
    val specialist = assignment.specialist
    val anyFreeLabel = stringResource(Res.string.edit_any_specialist)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBgInputEmpty, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (specialist != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(avatarBgColors[specialist.id % avatarBgColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = specialist.name.firstOrNull()?.uppercase() ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextSecondary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = specialist?.name ?: anyFreeLabel,
                fontSize = 15.sp,
                color = ColorTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ColorTextGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Service chip (edit sheet style) ───────────────────────────────────────────

@Composable
private fun EditServiceChip(label: String) {
    Box(
        modifier = Modifier
            .background(ColorBgInputEmpty, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = ColorTextPrimary)
    }
}

// ── Service picker content ────────────────────────────────────────────────────

@Composable
internal fun ServicePickerContent(
    selectedServices: Set<String>,
    onToggle: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
    ) {
        Text(
            text = stringResource(Res.string.edit_services_label),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        HorizontalDivider(color = ColorDivider)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mockServices) { service ->
                ServicePickerRow(
                    service = service,
                    isSelected = service.name in selectedServices,
                    onToggle = { onToggle(service.name) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = ColorDivider
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text(
                    text = stringResource(Res.string.edit_services_done),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Service picker row ────────────────────────────────────────────────────────

@Composable
private fun ServicePickerRow(
    service: ServiceItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "от ${service.priceFrom.formatThousands()} UZS · ${service.durationLabel}",
                fontSize = 13.sp,
                color = ColorTextHint
            )
        }

        Spacer(Modifier.width(12.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = ColorTextGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun Int.formatThousands(): String {
    val s = this.toString()
    val result = StringBuilder()
    s.forEachIndexed { i, c ->
        if (i > 0 && (s.length - i) % 3 == 0) result.append('\u00A0')
        result.append(c)
    }
    return result.toString()
}
