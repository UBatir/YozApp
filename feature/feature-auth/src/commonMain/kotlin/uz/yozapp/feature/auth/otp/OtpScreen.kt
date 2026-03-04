package uz.yozapp.feature.auth.otp

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgInputEmpty
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorError
import uz.yozapp.core.ui.theme.ColorPrimaryActive
import uz.yozapp.core.ui.theme.ColorTextCursor
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextMuted
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary
import uz.yozapp.feature.auth.name.NameScreen

data class OtpScreen(val phone: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model     = koinScreenModel<OtpScreenModel> { parametersOf(phone) }
        val state     by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    is Effect.NavigateToName -> navigator.push(NameScreen(phone = effect.phone))
                    Effect.NavigateBack      -> navigator.pop()
                }
            }
        }

        OtpContent(state = state, onIntent = model::handleIntent)
    }
}

@Composable
private fun OtpContent(
    state: State,
    onIntent: (Intent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        Spacer(Modifier.height(16.dp))

        IconButton(
            onClick = { onIntent(Intent.BackClicked) },
            modifier = Modifier.size(40.dp)
        ) {
            Text(
                text = stringResource(Res.string.back_arrow),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextPrimary
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.otp_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.otp_sent_to, formatPhone(state.phone)),
            fontSize = 15.sp,
            color = ColorTextSecondary
        )

        Spacer(Modifier.height(32.dp))

        // Скрытый BasicTextField перехватывает ввод с клавиатуры
        Box {
            BasicTextField(
                value = state.code,
                onValueChange = { onIntent(Intent.CodeChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                cursorBrush = SolidColor(Color.Transparent),
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester)
            )

            OtpBoxes(
                code = state.code,
                hasError = state.error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusRequester.requestFocus() }
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.error,
                fontSize = 13.sp,
                color = ColorError
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.otp_resend_timer, formatTimer(state.timerSeconds)),
            fontSize = 14.sp,
            color = ColorTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { onIntent(Intent.ResendClicked) },
            enabled = state.canResend,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorBgInputEmpty,
                disabledContainerColor = ColorBgInputEmpty
            )
        ) {
            Text(
                text = stringResource(Res.string.otp_btn_new_code),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (state.canResend) ColorTextPrimary else ColorTextHint
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun OtpBoxes(
    code: String,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            val digit     = code.getOrNull(index)
            val isFocused = index == code.length && code.length < 5

            val bgColor = if (digit != null || isFocused) Color.White else ColorBgInputEmpty
            val borderColor = when {
                hasError && digit != null -> ColorError
                isFocused                 -> ColorPrimaryActive
                digit != null             -> ColorPrimaryActive
                else                      -> Color.Transparent
            }
            val textColor   = if (hasError) ColorError else ColorPrimaryActive
            val borderWidth = if (isFocused || digit != null || hasError) 1.5.dp else 0.dp

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .background(bgColor, RoundedCornerShape(10.dp))
                    .border(borderWidth, borderColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isFocused  -> BlinkingCursor()
                    digit != null -> Text(
                        text = digit.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun BlinkingCursor() {
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing)
        ),
        label = "cursorAlpha"
    )
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(24.dp)
            .alpha(alpha)
            .background(ColorTextCursor)
    )
}

private fun formatPhone(phone: String): String {
    val digits = phone.removePrefix("+998")
    return if (digits.length == 9) {
        "+998 ${digits.substring(0, 2)} ${digits.substring(2, 5)} ${digits.substring(5, 7)} ${digits.substring(7, 9)}"
    } else {
        phone
    }
}

private fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}
