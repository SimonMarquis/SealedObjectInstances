plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    val kspVersion: String by project
    val kotlinVersion: String by project
    implementation(group = "com.google.devtools.ksp", name = "symbol-processing-api" , version = kspVersion)
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    kspTest(project(":processor"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.9")
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
    }
    publications {
        create<MavenPublication>("jitpack") {
            artifactId = "SealedObjectInstances"
            from(components["kotlin"])
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
