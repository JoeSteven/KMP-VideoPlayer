pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
}
rootProject.name = "KMP-VideoPlayer"


include(":android")
include(":desktop")
include(":core-lib")

include(":compose")
