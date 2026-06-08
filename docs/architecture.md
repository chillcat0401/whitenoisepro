# WhiteNoisePro Architecture

## Package and Modules

Root package:

```text
com.whitenoisepro
```

Gradle modules:

```text
:composeApp
```

The first MVP keeps a single Compose Multiplatform module to reduce setup overhead. Boundaries are enforced by package and source-set layout. Split into additional modules only when compile time, ownership, or platform isolation makes the cost worthwhile.

## Source Sets

```text
composeApp/src/commonMain/kotlin/com/whitenoisepro/
  app/                 App entry, navigation, shell state
  design/              Theme tokens and reusable Compose primitives
  domain/model/        Immutable domain models
  domain/reducer/      Pure reducer-style state transitions
  data/                Catalog, repositories, persistence interfaces
  playback/            Common PlaybackEngine boundary
  presentation/        Screen state and intent coordinators

composeApp/src/commonTest/kotlin/com/whitenoisepro/
  domain/              Model and reducer tests
  data/                Catalog and fake repository tests

composeApp/src/androidMain/kotlin/com/whitenoisepro/
  MainActivity.kt      Android entry point
  audio/               Android playback engine implementation
  platform/            Android-specific adapters
  storage/             Android persistence implementation
```

## Implementation Rules

- Common UI and domain code must not import Android, Media3, or platform storage APIs.
- Android audio and service code must sit behind the common `PlaybackEngine` boundary.
- Composables should use `design` tokens and primitives instead of scattered raw colors and dimensions.
- Build scripts should declare project structure and dependencies only. Production feature logic belongs in Kotlin source files.
- Figma and Stitch are visual contracts, not generated source code.

## Current Version Pins

```text
Gradle Wrapper: 8.14.5
Android Gradle Plugin: 8.13.2
Kotlin: 2.2.21
Compose Multiplatform: 1.11.1
Compile SDK: 36
Min SDK: 26
Target SDK: 36
```
