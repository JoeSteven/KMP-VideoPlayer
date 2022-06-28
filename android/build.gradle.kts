plugins {
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev709"
    id("com.android.application")
    kotlin("android")
}

group = "me.mimao"
version = "1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":sample"))
    implementation("androidx.activity:activity-compose:1.4.0")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "me.mimao.android"
        minSdkVersion(24)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}