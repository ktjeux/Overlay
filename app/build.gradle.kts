plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "fr.atelierreferences"
    compileSdk = 35
    defaultConfig {
        applicationId = "fr.atelierreferences"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
