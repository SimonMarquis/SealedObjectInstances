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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.nmcp)
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
    compilerOptions {
        allWarningsAsErrors = true
        jvmTarget = JVM_11
    }
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation.enabled = true
}

tasks.withType<AbstractArchiveTask>().configureEach {
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
    archiveClassifier = "sources"
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.javadoc)
    archiveClassifier = "javadoc"
}

dokka {
    moduleName.set("SealedObjectInstances")
    dokkaPublications.html {
        outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka"))
    }
    dokkaPublications.javadoc {
        outputDirectory.set(rootProject.layout.buildDirectory.dir("javadoc"))
    }
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier = "javadoc"
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier = "html-docs"
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
    }
    publications {
        register<MavenPublication>("SealedObjectInstances") {
            artifactId = project.property("artifactId") as String
            from(components.named("kotlin").get())
            artifact(sourcesJar)
            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)
            pom {
                name = "SealedObjectInstances"
                description = "A Kotlin Symbol Processor to list sealed object instances."
                url = "https://github.com/SimonMarquis/SealedObjectInstances"
                licenses {
                    license {
                        name = "Apache-2.0 license"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        name = "Simon Marquis"
                        email = "contact@simon-marquis.fr"
                    }
                }
                scm {
                    url = "https://github.com/SimonMarquis/SealedObjectInstances"
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

nmcp.publishAllPublicationsToCentralPortal {
    username = System.getenv("CENTRAL_PORTAL_USERNAME")
    password = System.getenv("CENTRAL_PORTAL_PASSWORD")
    publishingType = "AUTOMATIC"
}

dependencies {
    compileOnly(libs.ksp.api)
    compileOnly(libs.kotlin.reflect)
    kspTest(projects.processor)
    testRuntimeOnly(libs.junit.runtime)
    testImplementation(libs.ksp.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.api)
    testImplementation(libs.kotlin.compileTestingKsp)
    testImplementation(libs.kotlin.reflect)
}
