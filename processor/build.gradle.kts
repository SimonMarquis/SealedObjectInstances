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
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        allWarningsAsErrors.set(true)
        jvmTarget.set(JVM_11)
        freeCompilerArgs.add("-Xsuppress-version-warnings")
    }
}

tasks.withType<AbstractArchiveTask> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(SKIPPED, PASSED, FAILED)
        showExceptions = true
        showStackTraces = true
        exceptionFormat = FULL
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

val dokkaJavadoc by tasks.getting(DokkaTask::class) {
    moduleName.set("SealedObjectInstances")
    outputDirectory.set(rootProject.buildDir.resolve("javadoc"))
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaJavadoc)
    from(dokkaJavadoc.outputDirectory)
    archiveClassifier.set("javadoc")
}

val dokkaHtml by tasks.getting(DokkaTask::class) {
    moduleName.set("SealedObjectInstances")
    outputDirectory.set(rootProject.buildDir.resolve("dokka"))
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    from(dokkaHtml.outputDirectory)
    archiveClassifier.set("html-docs")
}

publishing {
    repositories {
        mavenLocal {
            url = uri(rootProject.layout.buildDirectory.dir(".m2/repository"))
        }
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/SimonMarquis/SealedObjectInstances")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            name = "OSSRH"
            url = when (version.toString().endsWith("-SNAPSHOT")) {
                true -> "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                false -> "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }.let(::uri)
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("SealedObjectInstances") {
            artifactId = project.property("artifactId") as String
            from(components.getByName("kotlin"))
            artifact(sourcesJar)
            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)
            pom {
                name.set("SealedObjectInstances")
                description.set("A Kotlin Symbol Processor to list sealed object instances.")
                url.set("https://github.com/SimonMarquis/SealedObjectInstances")
                licenses {
                    license {
                        name.set("Apache-2.0 license")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Simon Marquis")
                        email.set("contact@simon-marquis.fr")
                    }
                }
                scm {
                    url.set("https://github.com/SimonMarquis/SealedObjectInstances")
                }
            }
        }
    }
}

/* https://docs.gradle.org/current/userguide/signing_plugin.html */
signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey == null || signingPassword == null) return@signing
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
    isRequired = true
}

tasks.withType<Sign> {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13470")
}

tasks.withType<DokkaTask> {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.kspApi)
    compileOnly(libs.kotlinReflect)
    kspTest(projects.processor)
    testRuntimeOnly(libs.junitRuntime)
    testImplementation(libs.kspApi)
    testImplementation(libs.kotlinTest)
    testImplementation(libs.junitApi)
    testImplementation(libs.kotlinCompileTestingKsp)
    testImplementation(libs.kotlinReflect)
}
