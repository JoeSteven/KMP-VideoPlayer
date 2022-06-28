import org.jetbrains.compose.compose
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev709"
}

group = "com.mimao.kmp.videoplayer"
version = "1.0"

kotlin {
    ios()
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("com.google.android.exoplayer:exoplayer:2.18.0")
                implementation("com.google.android.exoplayer:extension-okhttp:2.18.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("uk.co.caprica:vlcj:4.7.1")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("uk.co.caprica:vlcj:4.7.1")
            }
        }
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}