package uz.yozapp.feature.auth.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.compose.koinInject
import uz.yozapp.core.ui.navigation.ScreenProvider
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgLight
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorBorderLight
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorGoogle
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorTextMuted
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary
import uz.yozapp.feature.auth.login.LoginScreen

class WelcomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenProvider = koinInject<ScreenProvider>()
        val model = koinScreenModel<WelcomeScreenModel>()
        val state by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    Effect.NavigateToLogin -> navigator.push(LoginScreen())
                    Effect.NavigateToHome -> navigator.replaceAll(screenProvider.homeScreen())
                }
            }
        }

        WelcomeContent(
            state = state,
            onIntent = model::handleIntent
        )
    }
}

@Composable
internal fun WelcomeContent(
    state: State,
    onIntent: (Intent) -> Unit
) {
    if (state.isCheckingAuth) {
        Box(modifier = Modifier.fillMaxSize().background(ColorBgWhite))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = stringResource(Res.string.welcome_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.welcome_subtitle),
            fontSize = 15.sp,
            color = ColorTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onIntent(Intent.LoginClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
        ) {
            Text(
                text = stringResource(Res.string.welcome_btn_login),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = ColorDivider)
            Text(
                text = stringResource(Res.string.welcome_divider_or),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = ColorTextMuted
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = ColorDivider)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onIntent(Intent.GoogleClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ColorBorderLight),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = ColorBgWhite)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GoogleLetterIcon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(Res.string.welcome_btn_google),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(ColorBgLight, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { onIntent(Intent.GuestClicked) },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.welcome_btn_guest),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun GoogleLetterIcon() {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorGoogle
        )
    }
}
