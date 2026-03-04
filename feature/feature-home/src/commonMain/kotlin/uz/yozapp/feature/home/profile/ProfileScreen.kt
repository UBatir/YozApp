package uz.yozapp.feature.home.profile

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import uz.yozapp.core.ui.navigation.ScreenProvider
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgLight
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextPrimary

// ── Screen ────────────────────────────────────────────────────────────────────

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator      = LocalNavigator.currentOrThrow
        val screenProvider = koinInject<ScreenProvider>()
        val model          = koinScreenModel<ProfileScreenModel>()
        val state          by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    ProfileEffect.NavigateBack      -> navigator.pop()
                    ProfileEffect.NavigateToWelcome -> navigator.replaceAll(screenProvider.welcomeScreen())
                    ProfileEffect.NavigateToEdit    -> { /* TODO */ }
                }
            }
        }

        if (state.isLoggedIn) {
            LoggedInProfileContent(state = state, onIntent = model::handleIntent)
        } else {
            GuestProfileContent(state = state, onIntent = model::handleIntent)
        }

        if (state.showLanguagePicker) {
            LanguagePickerDialog(
                selectedLanguage = state.selectedLanguage,
                onSelect  = { model.handleIntent(ProfileIntent.LanguageSelected(it)) },
                onDismiss = { model.handleIntent(ProfileIntent.DismissLanguagePicker) }
            )
        }
    }
}

// ── Logged-in profile ─────────────────────────────────────────────────────────

@Composable
private fun LoggedInProfileContent(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
    ) {
        ProfileTopBar(
            onBack   = { onIntent(ProfileIntent.BackClicked) },
            showEdit = true,
            onEdit   = { onIntent(ProfileIntent.EditClicked) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar + name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar(name = state.user.name)
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = state.user.name,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorTextPrimary
                )
            }

            HorizontalDivider(color = ColorDivider)
            InfoRow(label = stringResource(Res.string.profile_field_name), value = state.user.name)
            HorizontalDivider(color = ColorDivider)

            if (state.user.gender != null) {
                InfoRow(label = stringResource(Res.string.profile_field_gender), value = state.user.gender)
                HorizontalDivider(color = ColorDivider)
            }

            if (state.user.birthDate != null) {
                InfoRow(label = stringResource(Res.string.profile_field_birth_date), value = state.user.birthDate)
                HorizontalDivider(color = ColorDivider)
            }

            InfoRow(label = stringResource(Res.string.profile_field_phone), value = state.user.phone)
            HorizontalDivider(color = ColorDivider)

            EmailRow(
                email   = state.user.email,
                onClick = { onIntent(ProfileIntent.EmailClicked) }
            )
            HorizontalDivider(color = ColorDivider)

            NotificationsRow(
                enabled  = state.notificationsEnabled,
                onToggle = { onIntent(ProfileIntent.ToggleNotifications) }
            )
            HorizontalDivider(color = ColorDivider)

            SettingsArrowRow(
                label    = stringResource(Res.string.profile_language),
                subtitle = state.selectedLanguage.displayName(),
                onClick  = { onIntent(ProfileIntent.LanguageRowClicked) }
            )
            HorizontalDivider(color = ColorDivider)

            SettingsArrowRow(
                label   = stringResource(Res.string.profile_logout),
                onClick = { onIntent(ProfileIntent.LogoutClicked) }
            )
            HorizontalDivider(color = ColorDivider)

            SettingsArrowRow(
                label   = stringResource(Res.string.profile_delete_account),
                onClick = { onIntent(ProfileIntent.DeleteAccountClicked) }
            )

            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

// ── Guest profile ─────────────────────────────────────────────────────────────

@Composable
private fun GuestProfileContent(state: ProfileState, onIntent: (ProfileIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
    ) {
        ProfileTopBar(
            onBack   = { onIntent(ProfileIntent.BackClicked) },
            showEdit = false,
            onEdit   = {}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFAAAAAE).copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = stringResource(Res.string.profile_guest_name).firstOrNull()?.uppercase() ?: "Г",
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text       = stringResource(Res.string.profile_guest_name),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = ColorTextPrimary
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider(color = ColorDivider, thickness = 1.dp)
            SettingsArrowRow(
                label    = stringResource(Res.string.profile_language),
                subtitle = state.selectedLanguage.displayName(),
                onClick  = { onIntent(ProfileIntent.LanguageRowClicked) }
            )
            HorizontalDivider(color = ColorDivider, thickness = 1.dp)

            Spacer(Modifier.weight(1f))

            Text(
                text       = stringResource(Res.string.profile_guest_hint),
                fontSize   = 14.sp,
                color      = ColorTextGray,
                textAlign  = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick  = { onIntent(ProfileIntent.LoginClicked) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
            ) {
                Text(
                    text       = stringResource(Res.string.profile_btn_login),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun ProfileTopBar(onBack: () -> Unit, showEdit: Boolean, onEdit: () -> Unit) {
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
                tint               = ColorTextPrimary
            )
        }
        Text(
            text       = stringResource(Res.string.profile_title),
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color      = ColorTextPrimary,
            modifier   = Modifier.weight(1f)
        )
        if (showEdit) {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.profile_cd_edit),
                    tint               = ColorTextPrimary
                )
            }
        }
    }
}

// ── Avatar ────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color(0xFFAAAAAE).copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = name.firstOrNull()?.uppercase() ?: "?",
            fontSize   = 32.sp,
            fontWeight = FontWeight.Medium,
            color      = Color.White
        )
    }
}

// ── Info row (label + value, no arrow) ───────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = ColorTextGray)
        Spacer(Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, color = ColorTextPrimary)
    }
}

// ── Email row (interactive) ───────────────────────────────────────────────────

@Composable
private fun EmailRow(email: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (email != null) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(Res.string.profile_field_email), fontSize = 13.sp, color = ColorTextGray)
                Spacer(Modifier.height(2.dp))
                Text(text = email, fontSize = 16.sp, color = ColorTextPrimary)
            }
        } else {
            Text(
                text       = stringResource(Res.string.profile_add_email),
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium,
                color      = ColorTextPrimary,
                modifier   = Modifier.weight(1f)
            )
        }
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint               = ColorTextGray,
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ── Notifications row ─────────────────────────────────────────────────────────

@Composable
private fun NotificationsRow(enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = stringResource(Res.string.profile_notifications),
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium,
                color      = ColorTextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = stringResource(Res.string.profile_notifications_subtitle),
                fontSize = 13.sp,
                color    = ColorTextGray
            )
        }
        Switch(
            checked         = enabled,
            onCheckedChange = { onToggle() },
            colors          = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ColorPrimary
            )
        )
    }
}

// ── Settings arrow row ────────────────────────────────────────────────────────

@Composable
private fun SettingsArrowRow(label: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = label,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium,
                color      = ColorTextPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = ColorTextGray)
            }
        }
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint               = ColorTextGray,
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ── Language picker dialog ────────────────────────────────────────────────────

@Composable
private fun LanguagePickerDialog(
    selectedLanguage: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            AppLanguage.entries.forEach { lang ->
                val isSelected  = lang == selectedLanguage
                val displayName = lang.displayName()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) ColorBgLight else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { onSelect(lang) }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint               = ColorTextPrimary,
                            modifier           = Modifier.size(18.dp)
                        )
                    } else {
                        Spacer(Modifier.width(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(text = displayName, fontSize = 16.sp, color = ColorTextPrimary)
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun AppLanguage.displayName(): String = when (this) {
    AppLanguage.RUSSIAN -> stringResource(Res.string.profile_lang_russian)
    AppLanguage.UZBEK   -> stringResource(Res.string.profile_lang_uzbek)
    AppLanguage.ENGLISH -> stringResource(Res.string.profile_lang_english)
}