# Sealed Object Instances [![Status](https://github.com/SimonMarquis/SealedObjectInstances/actions/workflows/build.yml/badge.svg)](https://github.com/SimonMarquis/SealedObjectInstances/actions/workflows/build.yml) [![](https://jitpack.io/v/SimonMarquis/SealedObjectInstances.svg)](https://jitpack.io/#SimonMarquis/SealedObjectInstances)


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
sealed class Debug(override val isEnabled: Boolean = false): FeatureFlag() {
    object Logs: Debug(true)
    object Traces: Debug()
    object StrictMode: Debug()
}
```

```kotlin
// UI.kt
sealed class UI(override val isEnabled: Boolean = false): FeatureFlag() {
    object Animations: UI()
    object Framerate: UI()
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

## Setup

In the module's build script, apply the `com.google.devtools.ksp` plugin with the current Kotlin version and add this library to the list of dependencies.

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.7.20-1.0.6"
}

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

## Make IDE aware of generated code

By default, IntelliJ IDEA or other IDEs don't know about the generated code. So it will mark references to generated symbols unresolvable. To make an IDE be able to reason about the generated symbols, mark the following paths as generated source roots:

- Android project
  ```kotlin
  androidComponents.onVariants {
      kotlin.sourceSets.findByName(it.name)?.kotlin?.srcDirs(
          file("$buildDir/generated/ksp/${it.name}/kotlin")
      )
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

- Having multiple sealed classes/interfaces with the same name in the same package is currently not supported, and the KSP will fail.
- [KT-8970](https://youtrack.jetbrains.com/issue/KT-8970/Object-is-uninitialized-null-when-accessed-from-static-context-ex-companion-object-with-initialization-loop): Using the extension from a static context (e.g. `companion object`), will return `null` values. A simple solution is to delegate to a lazy initializer: `val values by lazy(MySealedClass::class::sealedObjectInstances)`.
