import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose
}

kotlin {
    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal"
                )
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal"
                )
            }
        }
    }

    sourceSets {
        val macosMain by creating {
            dependencies {
                implementation(projects.app.shared)
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

compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"), kotlin.targets.getByName("macosArm64"))
    distributions {
        targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg)
        packageName = "KmpVideoPlayer"
        packageVersion = "1.0.0"
        macOS {
            bundleID = "com.mimao.kmp.videoplayer.sample"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Versions.Java.jvmTarget
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
            binaryOptions["memoryModel"] = "experimental"
        }
    }
}


if (System.getProperty("os.arch") == "aarch64") {
    val runMacos by tasks.registering {
        dependsOn("runDebugExecutableMacosArm64")
    }
    val runMacosRelease by tasks.registering {
        dependsOn("runReleaseExecutableMacosArm64")
    }
} else {
    val runMacos by tasks.registering {
        dependsOn("runDebugExecutableMacosX64")
    }
    val runMacosRelease by tasks.registering {
        dependsOn("runReleaseExecutableMacosX64")
    }
}