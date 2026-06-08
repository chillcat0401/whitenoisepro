## Why

WhiteNoisePro needs a stable architecture before feature coding because the product depends on long-running audio playback, timer behavior, state restoration, and a dark mobile UI that must stay coherent across several screens. The project also needs to validate Google Play quickly while preserving a practical path for Huawei / HarmonyOS investigation.

## What Changes

- Establish Compose Multiplatform as the primary implementation stack, with Android as the first production target.
- Define a clean domain model for sounds, sound layers, mixes, playback state, timer state, and settings.
- Define an Android-first audio boundary backed by Media3 / ExoPlayer / MediaSessionService, while keeping common code independent from Android APIs.
- Define the MVP screen shell and navigation model for Home, Mixer, Library, Timer, Saved Mixes, Settings, and the persistent Mini Player.
- Define a design-token strategy based on the Chinese Stitch / Figma visual baseline without requiring perfect Figma component extraction.
- Define a Huawei / HarmonyOS path as a later spike using ovCompose or a native ArkTS / ArkUI adapter, not as a first-release requirement.
- Define architecture-first implementation tasks with TDD and verification gates before UI polish or audio expansion.

## Capabilities

### New Capabilities

- `app-shell-navigation`: Mobile app shell, top bars, bottom navigation, mini player placement, and screen routing.
- `design-system`: Dark visual system, typography, spacing, component primitives, and Figma/Stitch visual alignment.
- `sound-catalog`: Local sound catalog, categories, sound metadata, and offline asset strategy.
- `mix-management`: Current mix state, sound layers, per-layer volume, master volume, saved mixes, favorites, and recent mixes.
- `playback-engine`: Platform-abstracted playback engine, Android Media3 implementation contract, background playback, and notification control.
- `sleep-timer`: Timer presets, custom duration, fade-out, stop behavior, countdown, cancel, and extension behavior.
- `settings-compliance`: Settings screen, persistence, privacy/compliance surface, and Android/Huawei readiness constraints.

### Modified Capabilities

- None. This is the first OpenSpec change for the project.

## Impact

- Affected future code areas:
  - Gradle / Compose Multiplatform project structure
  - `commonMain` domain, reducers, repositories, theme, and UI state
  - `androidMain` audio engine, MediaSessionService, permissions, notification behavior, and Android resources
  - UI components mapped from the Chinese Figma/Stitch baseline
  - Local persistence for mixes and settings
  - Test harness for domain logic, reducers, timer logic, and Android playback integration
- External dependencies to evaluate during implementation:
  - Compose Multiplatform
  - Kotlin Multiplatform
  - AndroidX Media3 ExoPlayer and MediaSession
  - Kotlin coroutines / Flow
  - Kotlin serialization
  - A multiplatform settings or datastore strategy
- Platform impact:
  - Android is the production target for MVP.
  - Desktop preview may be used for faster visual iteration if it does not distort mobile layout.
  - iOS is out of scope for MVP but should not be structurally blocked.
  - HarmonyOS is an explicit spike path, not part of MVP release acceptance.
