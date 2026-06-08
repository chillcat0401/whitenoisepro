## Context

WhiteNoisePro is a white noise and sleep-sound app with a Chinese Stitch/Figma visual baseline and a product goal of Android-first market validation, later Huawei / HarmonyOS exploration, and no marketing-first home screen. The project currently has no production code, so this change establishes architecture before implementation.

The selected implementation direction is Compose Multiplatform with Android as the first production target. Official Kotlin documentation describes Compose Multiplatform as a JetBrains UI toolkit that supports shared Compose UI for Android, iOS, desktop, and web, with Android using Jetpack Compose artifacts. HarmonyOS is not an official Compose Multiplatform target. Tencent's ovCompose sample demonstrates an unofficial path for importing Compose Multiplatform into ArkUI through an OHOS target and Harmony project, so HarmonyOS remains a spike path rather than an MVP promise.

The Android audio layer must be treated as a first-class architecture concern. Android's Media3 documentation recommends hosting `Player` and `MediaSession` inside a `MediaSessionService` for background playback, which allows playback to continue outside the foreground activity and enables system media controls. Media3 ExoPlayer is the likely Android player implementation, but multi-layer looping needs a focused spike because white-noise mixing can require multiple synchronized players or a custom mixing strategy.

References:

- Compose Multiplatform platform scope: https://kotlinlang.org/docs/multiplatform/compose-multiplatform-and-jetpack-compose.html
- Compose Multiplatform project structure: https://kotlinlang.org/docs/multiplatform/compose-multiplatform-create-first-app.html
- Android Media3 background playback: https://developer.android.com/media/media3/session/background-playback
- Android Media3 ExoPlayer getting started: https://developer.android.com/media/media3/exoplayer/hello-world
- ovCompose sample: https://github.com/Tencent-TDS/ovCompose-sample

## Goals / Non-Goals

**Goals:**

- Build an Android-first Compose Multiplatform MVP architecture.
- Keep domain and UI state in common Kotlin where practical.
- Keep Android audio, notifications, foreground service, and permissions in Android-specific source sets.
- Map the Chinese Figma/Stitch design into code-native Compose components rather than relying on direct Figma-to-code output.
- Define clean boundaries for future iOS, desktop preview, and HarmonyOS research.
- Make state, timer, and playback behavior testable before deep UI polish.

**Non-Goals:**

- Do not implement production UI or audio in this architecture change.
- Do not promise HarmonyOS NEXT support in MVP.
- Do not use direct exported Figma image assets as the main icon system.
- Do not introduce cloud sync, accounts, analytics, ads, payments, or subscriptions in the first architecture pass.
- Do not make a web landing page or marketing shell.

## Decisions

### Decision 1: Compose Multiplatform, Android-first

Use Compose Multiplatform as the app stack, with Android as the first production target. Structure the project so shared UI, theme tokens, navigation state, domain models, and reducers live in common Kotlin. Keep Android-specific media service and platform integrations in Android source sets.

Alternatives considered:

- Flutter: faster broad cross-platform UI but less native for Android media service architecture and less aligned with Kotlin/Android audio internals.
- Android-only Kotlin/Jetpack Compose: strongest Android path but blocks shared UI and increases future rewrite cost.
- ArkTS/ArkUI-first: best for HarmonyOS NEXT but slows Google Play MVP and adds platform risk immediately.

### Decision 2: Layered architecture with explicit platform boundaries

Use a layered structure:

```text
Compose UI
  -> presentation state / intents
  -> domain reducers and use cases
  -> repositories and playback boundary
  -> platform implementations
```

Proposed module/source-set shape:

```text
composeApp/
  src/commonMain/kotlin/
    app/                App entry, navigation graph, shell
    design/             Theme tokens, typography, spacing, shared components
    domain/model/       Sound, SoundLayer, SoundMix, TimerState, PlaybackState
    domain/usecase/     Mix, timer, settings, catalog use cases
    presentation/       Screen state, intents, reducers/view models
    data/               Repository interfaces and in-memory/local defaults
  src/commonTest/kotlin/
    domain/             Reducer and model tests
    presentation/       State transition tests
  src/androidMain/kotlin/
    audio/              Media3 playback engine and MediaSessionService
    platform/           Android permissions, notification, lifecycle adapters
    storage/            Android persistence implementation
  src/androidUnitTest/kotlin/
  src/androidInstrumentedTest/kotlin/
```

If the generated Compose Multiplatform template chooses a different module name, adapt the names while preserving these boundaries.

### Decision 3: Audio engine abstraction before Media3 implementation

Define a common `PlaybackEngine` boundary with commands and observable state:

```text
play(mix)
pause()
stop()
setLayerVolume(layerId, volume)
setLayerMuted(layerId, muted)
setMasterVolume(volume)
setTimerFadeFactor(factor)
observePlaybackState()
```

Android implementation should start with Media3 and MediaSessionService. The first implementation spike must decide whether multi-layer playback uses:

- Multiple ExoPlayer instances controlled by one service-level coordinator.
- A single ExoPlayer playlist for non-overlapping playback, not enough for mixing.
- Android lower-level audio mixing if Media3 multi-instance looping is insufficient.

The UI must not depend on which strategy wins.

### Decision 4: State is reducer-driven and testable

Use immutable screen/domain state and explicit user intents for core behavior. This keeps logic testable before UI implementation:

```text
MixIntent.AddSound(soundId)
MixIntent.RemoveLayer(layerId)
MixIntent.SetLayerVolume(layerId, value)
MixIntent.SetMasterVolume(value)
TimerIntent.Start(duration)
TimerIntent.Tick(now)
TimerIntent.Cancel
PlaybackIntent.PlayCurrentMix
```

This supports TDD and avoids burying behavior in composable functions.

### Decision 5: Design tokens live in code

Use the Chinese Stitch design document as source for initial tokens:

```text
background: #111317
surfaceContainerLow: #1A1C20
surfaceContainer: #1E2024
surfaceContainerHigh: #282A2E
surfaceVariant: #333539
onSurface: #E2E2E8
onSurfaceVariant: #BEC9C7
primary: #8AD3CE
secondary: #ACCAE3
tertiary: #FFB599
```

Do not wait for perfect Figma variables. Components should reference semantic Compose theme tokens, not raw literals scattered through UI.

### Decision 6: Figma is a visual contract, not a code source

Use Figma/Stitch screenshots for layout, density, color, and copy. Do not use direct Figma-generated React/Tailwind as app code. Icons should be implemented through Compose-friendly vector assets or a stable icon mapping, not remote image URLs from Figma exports.

### Decision 7: HarmonyOS is a spike, not a release gate

Track HarmonyOS through a future spike:

```text
Can a small Compose Multiplatform screen render through ovCompose in a Harmony app?
Can it package into a HAP?
Can basic audio play?
Can background audio, notification, and timer behavior be implemented?
```

If the spike fails or becomes too costly, a future ArkTS/ArkUI adapter can reuse product specs, copy, assets, and domain concepts.

## Risks / Trade-offs

- [Risk] Media3 may not provide clean multi-layer looping with one player -> Mitigation: make multi-layer audio a spike and hide strategy behind `PlaybackEngine`.
- [Risk] Background playback permissions and notification behavior differ by Android version -> Mitigation: use MediaSessionService, document manifest permissions, add Android integration verification.
- [Risk] Compose Multiplatform iOS/Desktop/Web support may tempt premature scope expansion -> Mitigation: Android-first MVP; other targets are non-goals until Android loop is stable.
- [Risk] ovCompose can run demos but may not support production sleep-audio needs -> Mitigation: separate HarmonyOS spike from MVP and do not block Android release.
- [Risk] Figma imports still have white gutters or imperfect components -> Mitigation: use Figma as visual baseline, implement code-native theme/components, verify by screenshots.
- [Risk] Audio assets can bloat package size -> Mitigation: start with a small bundled catalog, document asset format/length/loop constraints, expand later.

## Migration Plan

This is a greenfield project, so there is no runtime migration.

Implementation should proceed in controlled phases:

1. Scaffold Compose Multiplatform project and tests.
2. Define domain models and reducers.
3. Define design tokens and component primitives.
4. Build static UI shell from local fake data.
5. Add local persistence.
6. Add Android playback spike and MediaSessionService.
7. Integrate playback, timer, and Mini Player.
8. Run visual and behavior verification.

Rollback strategy:

- If Compose Multiplatform scaffolding blocks Android delivery, fall back to Android-only Jetpack Compose while preserving the same domain models and OpenSpec requirements.
- If Media3 multi-layer playback blocks MVP, ship a constrained single/noise-layer audio MVP only after updating OpenSpec and receiving user confirmation.

## Open Questions

- Which exact Compose Multiplatform template layout will be used by the scaffolding tool in the local environment?
- Which audio asset format and loop length will provide the best package-size and seamless-loop trade-off?
- Should MVP include desktop preview for faster UI iteration, or keep Android-only builds to reduce Gradle complexity?
- Should saved mixes be persisted via multiplatform-settings, DataStore wrapper, or a simple JSON store in the first pass?
- When should the HarmonyOS spike be scheduled relative to Google Play MVP validation?
