plugins {
    kotlin("jvm") apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

repositories {
    mavenCentral()
}
