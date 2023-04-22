# Changelog

## [Unreleased]

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

[Unreleased]: https://github.com/SimonMarquis/SealedObjectInstances/compare/1.6.0...HEAD
[1.6.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.6.0
[1.5.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.5.0
[1.4.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.4.0
[1.3.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.3.0
[1.2.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.2.0
[1.1.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.1.0
[1.0.0]: https://github.com/SimonMarquis/SealedObjectInstances/releases/tag/1.0.0
