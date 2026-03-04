plugins {
    id("kmp-compose")
}

android {
    namespace = "uz.yozapp.core.ui"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "uz.yozapp.core.ui.res"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(compose.components.resources)
            api(libs.voyager.navigator)
            api(libs.voyager.screenmodel)
            api(libs.voyager.koin)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.kotlinx.coroutines)
        }
    }
}
