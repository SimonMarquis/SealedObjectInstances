@Suppress("DSL_SCOPE_VIOLATION") // https://github.com/gradle/gradle/issues/22797
plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.binaryCompatibilityValidator) apply false
    alias(libs.plugins.spotless)
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        val licenseHeader = rootProject.file("spotless/spotless.kt")
        format("misc") {
            target("**/*.md", "**/.gitignore")
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlin {
            target("src/**/*.kt")
            ktlint(libs.versions.ktlint.get())
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(licenseHeader)
            targetExclude("spotless/*.kt")
        }
        kotlinGradle {
            ktlint(libs.versions.ktlint.get())
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(licenseHeader, "(import|plugins|buildscript|dependencies|pluginManagement|rootProject|@Suppress)")
        }
    }
}

repositories {
    mavenCentral()
}
