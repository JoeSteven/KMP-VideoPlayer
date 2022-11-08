import org.jetbrains.compose.compose
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version Versions.compose
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
    }
    iosX64()
    iosArm64()
    macosX64()
    macosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
                api(compose.ui)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(project(":kvideoplayer"))
                // api("io.github.joesteven:kvideoplayer:1.0.0")
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.4.2")
                api("androidx.core:core-ktx:1.8.0")
                api("androidx.compose.material3:material3:${Versions.android_material3}")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.material)
            }
        }
        val darwinMain by creating {
            dependsOn(commonMain)
        }

        val iosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val iosX64Main by getting {
            dependsOn(darwinMain)
        }
        val macosX64Main by getting {
            dependsOn(darwinMain)
        }
        val macosArm64Main by getting {
            dependsOn(darwinMain)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
}