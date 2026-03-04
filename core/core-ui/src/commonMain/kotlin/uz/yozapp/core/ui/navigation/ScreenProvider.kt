package uz.yozapp.core.ui.navigation

import cafe.adriel.voyager.core.screen.Screen

interface ScreenProvider {
    fun homeScreen(): Screen
    fun welcomeScreen(): Screen
}
