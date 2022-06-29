plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "com.mimao.kmp.videoplayer"
version = "1.0"

kotlin {
    iosX64()
    iosArm64()
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

        val iosArm64Main by getting
        val iosX64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
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