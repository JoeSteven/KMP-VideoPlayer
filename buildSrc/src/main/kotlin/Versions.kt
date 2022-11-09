import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.2.0"

    object Kotlin {
        const val lang = "1.7.20"
        const val coroutines = "1.6.4"
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_11
    }

    const val android_material3 = "1.0.0-alpha13"

    const val exoPlayer = "2.18.1"
    const val vlcj = "4.7.1"
}