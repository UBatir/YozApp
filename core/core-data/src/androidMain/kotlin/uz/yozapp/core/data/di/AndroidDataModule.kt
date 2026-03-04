package uz.yozapp.core.data.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.dsl.module

private const val DATASTORE_FILE = "yozapp.preferences_pb"

val androidDataModule = module {
    single {
        val context = get<Context>()
        PreferenceDataStoreFactory.createWithPath {
            context.filesDir.resolve(DATASTORE_FILE).absolutePath.toPath()
        }
    }
}
