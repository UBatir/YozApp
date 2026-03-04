plugins {
    id("kmp-network")
}

android {
    namespace = "uz.yozapp.core.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.content.neg)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.auth)
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core)
            implementation(project(":core:core-data"))
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
