plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

repositories {
    mavenCentral()
}
