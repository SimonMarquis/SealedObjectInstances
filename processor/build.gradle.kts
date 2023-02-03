import org.jetbrains.dokka.gradle.DokkaTask
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

kotlin {
    jvmToolchain(11)
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

tasks.test {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaHtml by tasks.getting(DokkaTask::class) {
    moduleName.set("SealedObjectInstances")
    outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka").get().asFile)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

java {
    withSourcesJar()
    withJavadocJar()
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
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
            from(components["java"])
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

tasks.withType<Sign>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13470")
}

tasks.withType<DokkaTask> {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
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
