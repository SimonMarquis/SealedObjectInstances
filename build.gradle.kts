@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.binaryCompatibilityValidator) apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

repositories {
    mavenCentral()
}
