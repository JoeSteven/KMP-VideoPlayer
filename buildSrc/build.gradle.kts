plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.2")
    api(kotlin("gradle-plugin", version = "1.8.20"))
}