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
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.spotless.LineEnding.GIT_ATTRIBUTES_FAST_ALLSAME

plugins {
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
}

allprojects {
    if (providers.gradleProperty("spotless").map(String::toBoolean).orNull != true) return@allprojects
    apply<SpotlessPlugin>()
    extensions.configure<SpotlessExtension> {
        val licenseHeader = rootProject.file("spotless/spotless.kt")
        lineEndings = GIT_ATTRIBUTES_FAST_ALLSAME
        format("misc") {
            target("**/*.md", "**/.gitignore")
            endWithNewline()
        }
        kotlin {
            target("src/**/*.kt")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(licenseHeader)
            targetExclude("spotless/*.kt")
        }
        kotlinGradle {
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(licenseHeader, "(import|plugins|buildscript|dependencies|pluginManagement|rootProject|@Suppress)")
        }
    }
}
