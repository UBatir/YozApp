package uz.yozapp.core.network.di

import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import kotlinx.coroutines.flow.firstOrNull
import org.koin.dsl.module
import uz.yozapp.core.data.local.AppPreferences
import uz.yozapp.core.network.ApiService
import uz.yozapp.core.network.createHttpClient

val networkModule = module {
    single {
        val prefs = get<AppPreferences>()
        createHttpClient().config {
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = prefs.authToken.firstOrNull()
                        token?.let { BearerTokens(it, it) }
                    }
                }
            }
        }
    }
    single { ApiService(get()) }
}
