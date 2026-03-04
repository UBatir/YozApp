package uz.yozapp.core.data.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

private const val DATASTORE_FILE = "yozapp.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
val iosDataModule = module {
    single {
        @Suppress("UNCHECKED_CAST")
        val docDir = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String
        PreferenceDataStoreFactory.createWithPath {
            "$docDir/$DATASTORE_FILE".toPath()
        }
    }
}
