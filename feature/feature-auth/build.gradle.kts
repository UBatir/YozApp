plugins {
    id("kmp-compose")
}

android {
    namespace = "uz.yozapp.feature.auth"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(project(":core:core-ui"))
            implementation(project(":core:core-data"))
        }
    }
}
