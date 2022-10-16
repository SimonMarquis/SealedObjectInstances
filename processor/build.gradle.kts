plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.ksp)
    `maven-publish`
    alias(libs.plugins.binaryCompatibilityValidator)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kspApi)
    implementation(libs.kotlinReflect)
    kspTest(projects.processor)
    testImplementation(libs.kotlinTest)
    testImplementation(libs.junitApi)
    testRuntimeOnly(libs.junitRuntime)
    testImplementation(libs.kotlinCompileTestingKsp)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

tasks.test {
    useJUnitPlatform()
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
        create<MavenPublication>("SealedObjectInstances") {
            artifactId = project.property("artifactId") as String
            from(components["kotlin"])
            pom {
                name.set("SealedObjectInstances")
                description.set("A Kotlin Symbol Processor to list sealed object instances.")
                url.set("https://github.com/SimonMarquis/SealedObjectInstances")
                packaging = "aar"
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
