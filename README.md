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
