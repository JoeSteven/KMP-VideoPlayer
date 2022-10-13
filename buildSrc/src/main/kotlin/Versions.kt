import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.2.0-alpha01-dev755"

    object Kotlin {
        const val lang = "1.7.0"
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_11
    }

    const val android_material3 = "1.0.0-alpha13"
}