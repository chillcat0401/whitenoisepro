# Publishable Audio Baseline Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the silent playback placeholder with four reproducible, first-party, loopable noise assets suitable for meaningful closed testing.

**Architecture:** Common catalog and sample mixes expose only published sound IDs. Android resolves each ID to a dedicated raw resource. A deterministic Node generator creates periodic PCM WAV files and a machine-readable QA manifest.

**Tech Stack:** Compose Multiplatform, Kotlin tests, Android raw resources, Node.js, PCM WAV, inverse FFT.

---

### Task 1: Published Catalog

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/data/SoundCatalog.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/whitenoisepro/data/SampleContent.kt`
- Modify: `composeApp/src/commonTest/kotlin/com/whitenoisepro/data/SoundCatalogTest.kt`

- [ ] Write failing tests for the four published IDs and default mix references.
- [ ] Run targeted tests and confirm failure.
- [ ] Update catalog and sample mixes.
- [ ] Run targeted tests until green.

### Task 2: Android Resource Resolver

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidSoundResourceResolver.kt`
- Create: `composeApp/src/androidUnitTest/kotlin/com/whitenoisepro/audio/AndroidSoundResourceResolverTest.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/AndroidPlaybackEngine.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/WhiteNoiseMediaSessionService.kt`

- [ ] Write failing mapping and fallback tests.
- [ ] Implement resolver.
- [ ] Update layer playback to use its sound ID resource.
- [ ] Update MediaSession placeholder media item.
- [ ] Run Android tests and compile.

### Task 3: Generated Assets

**Files:**
- Create: `tools/generate_mvp_audio.mjs`
- Create: `composeApp/src/androidMain/res/raw/white_noise_loop.wav`
- Create: `composeApp/src/androidMain/res/raw/pink_noise_loop.wav`
- Create: `composeApp/src/androidMain/res/raw/brown_noise_loop.wav`
- Create: `composeApp/src/androidMain/res/raw/fan_loop.wav`
- Create: `docs/audio-assets/generated-audio-manifest.json`
- Delete: `composeApp/src/androidMain/res/raw/silence_loop.wav`

- [ ] Implement deterministic periodic synthesis and WAV writer.
- [ ] Generate all assets and manifest.
- [ ] Run generator verification mode.
- [ ] Inspect assets with ffprobe.
- [ ] Remove silence placeholder.

### Task 4: Verification and Archive

**Files:**
- Modify: `docs/release-readiness/audio-asset-qa.md`
- Modify: `docs/release-readiness/google-play-checklist.md`
- Modify: `docs/android-playback-spike.md`
- Create: `docs/code-review-publishable-audio-2026-06-07.md`

- [ ] Update current-state and remaining-risk documentation.
- [ ] Run full Gradle verification.
- [ ] Inspect APK resource entries.
- [ ] Strictly validate and archive OpenSpec.
