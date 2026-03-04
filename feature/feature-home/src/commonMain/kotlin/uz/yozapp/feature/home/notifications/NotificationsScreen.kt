package uz.yozapp.feature.home.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary

// ── Screen ────────────────────────────────────────────────────────────────────

class NotificationsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model     = koinScreenModel<NotificationsScreenModel>()
        val state     by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    NotificationsEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        NotificationsContent(state = state, onIntent = model::handleIntent)
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsContent(state: NotificationsState, onIntent: (NotificationsIntent) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NotificationsTopBar(onBack = { onIntent(NotificationsIntent.BackClicked) })
            HorizontalDivider(color = ColorDivider, thickness = 1.dp)

            if (state.notifications.isEmpty()) {
                NotificationsEmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.notifications, key = { it.id }) { notif ->
                        NotificationRow(
                            item = notif,
                            onClick = { onIntent(NotificationsIntent.ItemClicked(notif)) }
                        )
                        HorizontalDivider(color = ColorDivider, thickness = 0.5.dp)
                    }
                }
            }

            // Footer: mark all as read
            if (state.notifications.any { !it.isRead }) {
                HorizontalDivider(color = ColorDivider, thickness = 1.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onIntent(NotificationsIntent.MarkAllRead) }
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓  ${stringResource(Res.string.notifications_mark_read)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorPrimary
                    )
                }
            } else {
                Spacer(Modifier.navigationBarsPadding())
            }
        }

        // Snackbar
        AnimatedVisibility(
            visible = state.snackbarVisible,
            enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(300)) { it } + fadeOut(tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF222222), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(Res.string.notifications_all_read),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Bottom sheet
    if (state.selectedItem != null) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(NotificationsIntent.DismissSheet) },
            sheetState = sheetState,
            containerColor = ColorBgWhite,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            NotificationDetailSheet(
                item = state.selectedItem,
                onDismiss = { onIntent(NotificationsIntent.DismissSheet) }
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.home_cd_back),
                tint = ColorTextPrimary
            )
        }
        Text(
            text = stringResource(Res.string.notifications_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun NotificationsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = ColorTextHint,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.notifications_empty),
            fontSize = 15.sp,
            color = ColorTextGray
        )
    }
}

// ── Notification row ──────────────────────────────────────────────────────────

@Composable
private fun NotificationRow(item: NotificationItem, onClick: () -> Unit) {
    val bgColor    = if (!item.isRead) Color(0xFFEEECFF) else ColorBgWhite
    val iconBgColor = if (!item.isRead) Color(0xFFDDDAFF) else Color(0xFFF0F0F0)
    val iconEmoji  = when (item.type) {
        NotificationType.REVIEW     -> "⭐"
        NotificationType.REMINDER   -> "🔔"
        NotificationType.RESCHEDULE -> "📅"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(iconBgColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = item.time, fontSize = 11.sp, color = ColorTextGray)
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.body,
                fontSize = 13.sp,
                color = ColorTextSecondary,
                lineHeight = 18.sp
            )
            if (item.type == NotificationType.REVIEW && !item.isRead) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(ColorPrimary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_action_rate),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Detail bottom sheet ───────────────────────────────────────────────────────

@Composable
private fun NotificationDetailSheet(item: NotificationItem, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        Text(text = item.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(text = item.body, fontSize = 15.sp, color = ColorTextSecondary, lineHeight = 22.sp)
        Spacer(Modifier.height(24.dp))

        when (item.type) {
            NotificationType.REVIEW -> {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_action_rate),
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTextPrimary)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_action_not_now),
                        fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ColorTextPrimary
                    )
                }
            }

            NotificationType.REMINDER -> {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_action_view_booking),
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                }
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_close),
                        fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ColorTextPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.notifications_sheet_footer),
                    fontSize = 12.sp, color = ColorTextHint,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            NotificationType.RESCHEDULE -> {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text(
                        text = stringResource(Res.string.notifications_action_ok),
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.notifications_sheet_footer),
                    fontSize = 12.sp, color = ColorTextHint,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}