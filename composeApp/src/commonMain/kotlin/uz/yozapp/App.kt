package uz.yozapp

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import uz.yozapp.core.ui.theme.YozAppTheme
import uz.yozapp.feature.auth.welcome.WelcomeScreen

@Composable
fun App() {
    YozAppTheme {
        Navigator(WelcomeScreen())
    }
}
