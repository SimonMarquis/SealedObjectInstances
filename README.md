# Sealed Object Instances [![Status](https://github.com/SimonMarquis/SealedObjectInstances/actions/workflows/build.yml/badge.svg)](https://github.com/SimonMarquis/SealedObjectInstances/actions/workflows/build.yml)


> A **K**otlin **S**ymbol **P**rocessor to list sealed object instances.

## Usage

Let's say you have a similar structure of sealed classes (or interfaces):

```kotlin
// FeatureFlag.kt
sealed class FeatureFlag {
    abstract val isEnabled: Boolean
}
```

```kotlin
// Debug.kt
sealed class Debug(override val isEnabled: Boolean = false) : FeatureFlag() {
    data object Logs : Debug(true)
    data object Traces : Debug()
    data object StrictMode : Debug()
}
```

```kotlin
// UI.kt
sealed class UI(override val isEnabled: Boolean = false) : FeatureFlag() {
    data object Animations : UI()
    data object Framerate : UI()
}
```

And you want to automatically list all `FeatureFlag`s, then you have at least 3 options:
- 🙅 Manually keep a list of objects somewhere, but this is cumbersome and error-prone.
- 🤷 Use [Kotlin reflection](https://kotlinlang.org/docs/reflection.html) to browse the class declaration, with the following extension `FeatureFlag::class.reflectSealedObjectInstances()`, but this can become an expensive operation if you have a lot of objects and a deeply nested structure.
- 🙆 Use [Kotlin Symbol Processing](https://kotlinlang.org/docs/ksp-overview.html) (KSP) to create the list of objects at compile time.

This is exactly what this project does.
You simply need to add the `SealedObjectInstances` annotation on the sealed class:

```kotlin
@SealedObjectInstances
sealed class FeatureFlag { /*...*/ }
```

And then you'll have access to the `sealedObjectInstances()` extension on the corresponding type:

```kotlin
val flags: Set<FeatureFlag> = FeatureFlag::class.sealedObjectInstances()
```

> [!NOTE]  
> You can annotate the `companion object` to access a simpler extension function (no more `::class` prefix).
> ```kotlin
> sealed class FeatureFlag {
>     @SealedObjectInstances companion object;
>     /*...*/
> }
>
> val flags: Set<FeatureFlag> = FeatureFlag.sealedObjectInstances()
> ```

## Setup

In the module's build script, apply the `com.google.devtools.ksp` plugin with the matching Kotlin version: [![Maven Central](https://img.shields.io/maven-central/v/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin?label=%20&color=success)](https://central.sonatype.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin)

```kotlin
plugins {
    id("com.google.devtools.ksp") version "<version>"
}
```

Then add the library dependencies: [![Maven Central](https://img.shields.io/maven-central/v/fr.smarquis.sealed/sealed-object-instances?label=%20&color=success)](https://central.sonatype.com/artifact/fr.smarquis.sealed/sealed-object-instances) [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/fr.smarquis.sealed/sealed-object-instances?label=%20&color=lightgrey&server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/snapshots/fr/smarquis/sealed/sealed-object-instances/)

```kotlin
dependencies {
    implementation("fr.smarquis.sealed:sealed-object-instances:<latest-version>")
    ksp("fr.smarquis.sealed:sealed-object-instances:<latest-version>")
}
```

<details><summary>🐙 GitHub Packages</summary>


[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/SimonMarquis/SealedObjectInstances?label=%20&color=success)](https://github.com/SimonMarquis/SealedObjectInstances/packages/1674974/versions)

> [!NOTE]  
> You'll need to create a personal access token (PAT) with the `read:packages` permission to be able to download from this repository.
> https://docs.github.com/en/packages/learn-github-packages

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/SimonMarquis/SealedObjectInstances")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_PACKAGES_READ_TOKEN")
        }
    }
}

dependencies {
    implementation("fr.smarquis.sealed:sealed-object-instances:<latest-version>")
    ksp("fr.smarquis.sealed:sealed-object-instances:<latest-version>")
}
```

</details>

<details><summary>🚀 JitPack.io</summary>


[![JitPack.io](https://img.shields.io/jitpack/version/com.github.SimonMarquis/SealedObjectInstances?color=success&label=%20)](https://jitpack.io/#SimonMarquis/SealedObjectInstances)

```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.SimonMarquis:SealedObjectInstances:<latest-version>")
    ksp("com.github.SimonMarquis:SealedObjectInstances:<latest-version>")
}
```

</details>

## Make IDE aware of generated code

By default, IntelliJ IDEA or other IDEs don't know about the generated code. So it will mark references to generated symbols unresolvable. To make an IDE be able to reason about the generated symbols, mark the following paths as generated source roots:

- Android project
  ```kotlin
  androidComponents.beforeVariants {
    kotlin.sourceSets.register(it.name) {
        kotlin.srcDir(file("$buildDir/generated/ksp/${it.name}/kotlin"))
    }
  }
  ```
- Kotlin JVM project
  ```kotlin
  kotlin {
      sourceSets.main {
          kotlin.srcDir("build/generated/ksp/main/kotlin")
      }
      sourceSets.test {
          kotlin.srcDir("build/generated/ksp/test/kotlin")
      }
  }
  ```

## Configuration

`SealedObjectInstances` annotation can be configured to produce different outputs, and is also repeatable:

```kotlin
@SealedObjectInstances
@SealedObjectInstances(name = "values", rawType = Array)
sealed class FeatureFlag { /*...*/ }
```

This code will produce these two extensions:

```kotlin
// The default extension
fun KClass<FeatureFlag>.sealedObjectInstances(): Set<FeatureFlag>
// The custom extension with different name and raw type
fun KClass<FeatureFlag>.values(): Array<FeatureFlag>
```

## Known issues

- Having multiple sealed classes/interfaces with the same name in the same package is currently not supported, and the KSP will fail. But this can be avoided by providing a custom generated `fileName` on the `SealedObjectInstances` annotation.
- [KT-8970](https://youtrack.jetbrains.com/issue/KT-8970/Object-is-uninitialized-null-when-accessed-from-static-context-ex-companion-object-with-initialization-loop): Using the extension from a static context (e.g. `companion object`), will return `null` values. A simple solution is to delegate to a lazy initializer: `val values by lazy(MySealedClass::class::sealedObjectInstances)`.
- Does not support non-standard (alphanumeric) class names that needs backticks like:
  ```kotlin
  sealed class `A-B-C`
  ```
- [google/ksp#1651](https://github.com/google/ksp/issues/1651): KSP does not run on common multiplatform code, requiring an extra manual ["bridge" expect/actual method](https://github.com/SimonMarquis/SealedObjectInstances/blob/main/multiplatform/src/commonMain/kotlin/com/example/Entity.kt#L30).
