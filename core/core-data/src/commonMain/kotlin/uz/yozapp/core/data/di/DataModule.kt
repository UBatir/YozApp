package uz.yozapp.core.data.di

import org.koin.dsl.module
import uz.yozapp.core.data.local.AppPreferences

val dataModule = module {
    single { AppPreferences(get()) }
}
