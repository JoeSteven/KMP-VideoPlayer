plugins {
    id("org.jetbrains.compose") version Versions.compose
    id("com.android.application")
    kotlin("android")
}

group = "me.mimao"
version = "1.0"

dependencies {
    implementation(projects.app.shared)
    implementation("androidx.activity:activity-compose:1.4.0")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "me.mimao.android"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}