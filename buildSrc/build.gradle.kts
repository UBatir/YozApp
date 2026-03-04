plugins { `kotlin-dsl` }

dependencies {
    // Marker artifacts — each provides version metadata + transitively pulls the actual plugin JAR.
    // This allows convention plugins to use id() without version, and avoids
    // the "already on classpath with unknown version" error in module build files.
    implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.3.10")
    implementation("com.android.library:com.android.library.gradle.plugin:8.13.2")
    implementation("com.android.application:com.android.application.gradle.plugin:8.13.2")
    implementation("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.10.1")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.3.10")
    implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:2.3.10")
}
