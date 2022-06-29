import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.2.0-alpha01-dev709"

    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }

    const val android_material3 = "1.0.0-alpha13"
}