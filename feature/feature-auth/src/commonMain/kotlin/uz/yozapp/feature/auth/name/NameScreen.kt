package uz.yozapp.feature.auth.name

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import uz.yozapp.core.ui.navigation.ScreenProvider
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgInput
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorBorderMedium
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary

data class NameScreen(val phone: String) : Screen {

    @Composable
    override fun Content() {
        val navigator      = LocalNavigator.currentOrThrow
        val screenProvider = koinInject<ScreenProvider>()
        val model          = koinScreenModel<NameScreenModel> { parametersOf(phone) }
        val state          by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    Effect.NavigateToHome -> navigator.replaceAll(screenProvider.homeScreen())
                    Effect.NavigateBack   -> navigator.pop()
                }
            }
        }

        NameContent(state = state, onIntent = model::handleIntent)
    }
}

@Composable
private fun NameContent(
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
            modifier = Modifier.height(40.dp)
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
            text = stringResource(Res.string.name_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.name_subtitle),
            fontSize = 15.sp,
            color = ColorTextSecondary
        )

        Spacer(Modifier.height(32.dp))

        NameTextField(
            name = state.name,
            onValueChange = { onIntent(Intent.NameChanged(it)) },
            focusRequester = focusRequester
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { onIntent(Intent.ContinueClicked) },
            enabled = state.canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorPrimary,
                disabledContainerColor = ColorDivider
            )
        ) {
            Text(
                text = stringResource(Res.string.name_btn_continue),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (state.canContinue) Color.White else ColorTextHint
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun NameTextField(
    name: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val bgColor     = if (isFocused) Color.White else ColorBgInput
    val borderColor = if (isFocused) ColorBorderMedium else Color.Transparent
    val borderWidth = if (isFocused) 1.dp else 0.dp

    BasicTextField(
        value = name,
        onValueChange = onValueChange,
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = ColorTextPrimary,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(ColorPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(bgColor, RoundedCornerShape(12.dp))
                    .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (name.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.name_placeholder),
                        fontSize = 16.sp,
                        color = ColorTextHint
                    )
                }
                innerTextField()
            }
        }
    )
}
