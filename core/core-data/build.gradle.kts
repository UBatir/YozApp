plugins {
    id("kmp-library")
}

android {
    namespace = "uz.yozapp.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.datastore.prefs)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
