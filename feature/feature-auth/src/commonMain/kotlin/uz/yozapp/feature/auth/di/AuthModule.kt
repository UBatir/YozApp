package uz.yozapp.feature.auth.di

import org.koin.dsl.module
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.feature.auth.login.LoginScreenModel
import uz.yozapp.feature.auth.name.NameScreenModel
import uz.yozapp.feature.auth.otp.OtpScreenModel
import uz.yozapp.feature.auth.welcome.WelcomeScreenModel

val authModule = module {
    factory { WelcomeScreenModel(get<AppPreferences>()) }
    factory { LoginScreenModel() }
    factory { (phone: String) -> OtpScreenModel(phone = phone) }
    factory { (phone: String) -> NameScreenModel(phone = phone, prefs = get<AppPreferences>()) }
}
