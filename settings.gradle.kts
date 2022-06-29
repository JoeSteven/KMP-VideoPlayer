pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "KMP-VideoPlayer"

include(":core-lib")
include(":app:shared",":app:android", "app:desktop", "app:uikit" )

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")