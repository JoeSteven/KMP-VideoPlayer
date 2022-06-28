plugins {
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    kotlin("android").apply(false)
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            // allWarningsAsErrors = true
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
                "-Xskip-prerelease-check",
            )
        }
    }
    // repositories {
    //     google()
    //     mavenCentral()
    //     maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    // }
}