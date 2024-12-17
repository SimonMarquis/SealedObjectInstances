# Changelog

## [Unreleased]

- Provide default proguard file to prevent errors on Android consumers
- Add basic unit tests in app module in https://github.com/SimonMarquis/SealedObjectInstances/pull/238
- Kotlin Multiplatform example in https://github.com/SimonMarquis/SealedObjectInstances/pull/239
- Gradle dependency signature verification in https://github.com/SimonMarquis/SealedObjectInstances/pull/307

## [1.8.0]

_2023-07-09_

- Update Kotlin 1.9.0 in https://github.com/SimonMarquis/SealedObjectInstances/pull/89
- Update Gradle 8.2 in https://github.com/SimonMarquis/SealedObjectInstances/pull/114
- Migrate compiler tests to [ZacSweers/kotlin-compile-testing](https://github.com/ZacSweers/redacted-compiler-plugin) in https://github.com/SimonMarquis/SealedObjectInstances/pull/115

## [1.7.0]

_2023-06-18_

- Fix #98 by removing unexpected generics marker on Companion extension
- Constraint dependencies to not leak through consumers (KSP-api and Kotlin reflect)
- Remove unnecessarily enforced `kotlin.jvmToolchain(17)`
- Support inferred visibility of Companion object based on sealed class visibility

## [1.6.3]

_2023-04-29_

- Downgrade compatibility version to JVM 11 @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/83

## [1.6.2]

_2023-04-23_

- 🩹 Apply visibility modifier to Companion object extensions by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/76

## [1.6.1]

_2023-04-23_

- 🩹 Ignore duplicated annotations by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/75
- Update dependency gradle to v8.1.1 by @renovate in https://github.com/SimonMarquis/SealedObjectInstances/pull/68

## [1.6.0]

_2023-04-22_

- Update Kotlin 1.8.20
- Update KSP 1.8.20-1.0.11
- Update Gradle 8.1
- 🆕 Add support for extensions on the companion object by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/73
- Add support for Spotless by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/53
- Enable Gradle local build cache by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/37
- Enable workflow_dispatch for build workflow by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/36
- Fix publication artifacts by manually selecting Dokka variants by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/26
- ⚙️ Disable Gradle configuration cache by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/15
- ⚙️ Configure JVM toolchain by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/14
- 📝 Update KSP version in README by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/13
- ⚙️ Correctly disable configuration cache for Dokka tasks by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/12
- ⚙️ Configure JVM toolchain first by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/11
- 🆕 Add toggle for return type on the generated method by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/10
- 📦 Update Kotlin 1.8.10 and KSP 1.8.10-1.0.9 by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/9
- 🧪 Add new tests with bounded types by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/8
- 🐛 Fix wrong type in tests by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/7
- ⚙️ Enable allWarningsAsErrors by @SimonMarquis in https://github.com/SimonMarquis/SealedObjectInstances/pull/6

## [1.5.0]

_2022-11-02_

- Add a `fileName` parameter to `SealedObjectInstances` annotation
- Migrate default javadoc to Dokka

## [1.4.0]

_2022-09-30_

- Update Kotlin 1.7.20

## [1.3.0]

_2022-09-14_

- Add support for visibility modifier
- Add support for generic type parameters

## [1.2.0]

_2022-09-01_

- Allow nested references to extensions by removing unnecessary KSP validation step

## [1.1.0]

_2022-08-28_

- Add `@SealedObjectInstances` parameters to configure method name and raw type
- Support repeatable `@SealedObjectInstances` annotations to produce different extensions

## [1.0.0]

_2022-08-22_

> **Note** 🎉 Initial release

[Unreleased]: https://github.com/SimonMarquis/SealedObjectInstances/compare/1.8.0...HEAD
[1.8.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.8.0
[1.7.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.7.0
[1.6.3]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.6.3
[1.6.2]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.6.2
[1.6.1]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.6.1
[1.6.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.6.0
[1.5.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.5.0
[1.4.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.4.0
[1.3.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.3.0
[1.2.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.2.0
[1.1.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.1.0
[1.0.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.0.0
