package uz.yozapp

import org.koin.core.context.startKoin
import uz.yozapp.core.data.di.iosDataModule
import uz.yozapp.di.appModules

fun initKoin() {
    startKoin {
        modules(appModules + iosDataModule)
    }
}
