import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.2.0-alpha01-dev716"

    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }
}