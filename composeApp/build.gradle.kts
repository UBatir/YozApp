plugins {
    id("android-app")
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.koin.android)
            implementation(libs.yandex.maps)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.koin.core)
            implementation(project(":feature:feature-auth"))
            implementation(project(":feature:feature-home"))
            implementation(project(":core:core-ui"))
            implementation(project(":core:core-network"))
            implementation(project(":core:core-data"))
        }
    }
}

android {
    namespace = "uz.yozapp"

    defaultConfig {
        applicationId = "uz.yozapp"
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
