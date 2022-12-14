plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.ksp)
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    implementation(projects.processor)
    ksp(projects.processor)
}
