package uz.yozapp.feature.auth.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import uz.yozapp.core.ui.theme.ColorBorderMedium
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorPrimaryDisabled
import uz.yozapp.core.ui.theme.ColorTextCursor
import uz.yozapp.core.ui.theme.ColorTextDisabled
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary
import uz.yozapp.feature.auth.otp.OtpScreen

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<LoginScreenModel>()
        val state by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    is Effect.NavigateToOtp -> navigator.push(OtpScreen(phone = effect.phone))
                    Effect.NavigateBack -> navigator.pop()
                }
            }
        }

        LoginContent(
            state = state,
            onIntent = model::handleIntent
        )
    }
}

@Composable
internal fun LoginContent(
    state: State,
    onIntent: (Intent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    val termsPrefix = stringResource(Res.string.login_terms_prefix)
    val termsLink = stringResource(Res.string.login_terms_link)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        IconButton(
            onClick = { onIntent(Intent.BackClicked) },
            modifier = Modifier.size(40.dp)
        ) {
            Text(
                text = stringResource(Res.string.back_arrow),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextPrimary
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.login_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.login_subtitle),
            fontSize = 15.sp,
            color = ColorTextSecondary
        )

        Spacer(Modifier.height(32.dp))

        BasicTextField(
            value = state.phoneNumber,
            onValueChange = { onIntent(Intent.PhoneChanged(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            decorationBox = {
                PhoneNumberDisplay(phoneNumber = state.phoneNumber)
            }
        )

        Spacer(Modifier.weight(1f))

        val termsText = buildAnnotatedString {
            withStyle(SpanStyle(color = ColorTextGray, fontSize = 13.sp)) {
                append(termsPrefix)
            }
            withStyle(SpanStyle(color = ColorPrimary, fontSize = 13.sp)) {
                append(termsLink)
            }
        }
        Text(
            text = termsText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onIntent(Intent.GetCodeClicked) },
            enabled = state.isPhoneComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorPrimary,
                disabledContainerColor = ColorPrimaryDisabled
            )
        ) {
            Text(
                text = stringResource(Res.string.login_btn_get_code),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (state.isPhoneComplete) Color.White else ColorTextDisabled
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

private val PHONE_GROUPS = listOf(2, 3, 2, 2)

@Composable
private fun PhoneNumberDisplay(phoneNumber: String) {
    val isComplete = phoneNumber.length == 9

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "+998 ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )

        var digitIndex = 0
        PHONE_GROUPS.forEachIndexed { groupIndex, groupSize ->
            if (groupIndex > 0) Spacer(Modifier.width(8.dp))
            repeat(groupSize) {
                if (digitIndex < phoneNumber.length) {
                    Text(
                        text = phoneNumber[digitIndex].toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextPrimary
                    )
                    if (!isComplete && digitIndex == phoneNumber.length - 1) {
                        BlinkingCursor()
                    }
                } else {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(ColorBorderMedium, CircleShape)
                    )
                }
                digitIndex++
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
            .padding(horizontal = 2.dp)
            .width(2.dp)
            .height(30.dp)
            .alpha(alpha)
            .background(ColorTextPrimary)
    )
}