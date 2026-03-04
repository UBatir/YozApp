package uz.yozapp.di

import org.koin.dsl.module
import uz.yozapp.core.data.di.dataModule
import uz.yozapp.core.network.di.networkModule
import uz.yozapp.core.ui.navigation.ScreenProvider
import uz.yozapp.feature.auth.di.authModule
import uz.yozapp.feature.auth.welcome.WelcomeScreen
import uz.yozapp.feature.home.home.HomeScreen
import uz.yozapp.feature.home.di.homeModule

private val navigationModule = module {
    single<ScreenProvider> {
        object : ScreenProvider {
            override fun homeScreen() = HomeScreen()
            override fun welcomeScreen() = WelcomeScreen()
        }
    }
}

val appModules = listOf(navigationModule, dataModule, networkModule, authModule, homeModule)
