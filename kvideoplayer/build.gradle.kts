import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

kotlin {
    ios {
        compilations.getByName("main") {
            cinterops {
                val observer by creating {
                    defFile(project.file("src/nativeInterop/cinterop/observer.def"))
                    packageName("com.mimao.kmp.videoplayer")
                }
            }
        }
    }
    iosArm64()
    iosX64()
    macosX64 {
        compilations.getByName("main") {
            cinterops {
                val observer by creating {
                    defFile(project.file("src/nativeInterop/cinterop/observer.def"))
                    packageName("com.mimao.kmp.videoplayer")
                }
            }
        }
    }
    macosArm64 {
        compilations.getByName("main") {
            cinterops {
                val observer by creating {
                    defFile(project.file("src/nativeInterop/cinterop/observer.def"))
                    packageName("com.mimao.kmp.videoplayer")
                }
            }
        }
    }
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
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
                api("com.google.android.exoplayer:exoplayer:2.18.0")
                api("com.google.android.exoplayer:extension-okhttp:2.18.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                api("uk.co.caprica:vlcj:4.7.1")
            }
        }
        val darwinMain by creating {
            dependsOn(commonMain)
        }
        val iosMain by getting{
            dependsOn(darwinMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val macosMain by creating {
            dependencies {
                dependsOn(darwinMain)
            }
        }
        val macosX64Main by getting {
            dependsOn(macosMain)
        }

        val macosArm64Main by getting {
            dependsOn(macosMain)
        }

    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
}

@Suppress("UnstableApiUsage")
configure<MavenPublishBaseExtension> {
    configure(KotlinMultiplatform())
}