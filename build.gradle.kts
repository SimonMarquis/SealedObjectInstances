/*
 * Copyright (C) 2023 Simon Marquis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
