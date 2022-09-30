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
