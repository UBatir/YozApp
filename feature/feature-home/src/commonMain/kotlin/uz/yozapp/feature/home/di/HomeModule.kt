package uz.yozapp.feature.home.di

import org.koin.dsl.module
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.feature.home.bookings.BookingsScreenModel
import uz.yozapp.feature.home.favorites.FavoritesScreenModel
import uz.yozapp.feature.home.home.HomeScreenModel
import uz.yozapp.feature.home.notifications.NotificationsScreenModel
import uz.yozapp.feature.home.profile.ProfileScreenModel
import uz.yozapp.feature.home.sectionlist.SectionListScreenModel

val homeModule = module {
    factory { HomeScreenModel(get<AppPreferences>()) }
    factory { BookingsScreenModel() }
    factory { FavoritesScreenModel() }
    factory { NotificationsScreenModel() }
    factory { ProfileScreenModel(get<AppPreferences>()) }
    factory { (title: String, sectionIndex: Int) -> SectionListScreenModel(title, sectionIndex) }
}
