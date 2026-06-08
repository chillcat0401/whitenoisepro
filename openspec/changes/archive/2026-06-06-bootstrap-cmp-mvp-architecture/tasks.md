# Tasks

## 1. Architecture Scaffolding

- [x] 1.1 Create Compose Multiplatform project scaffold
  - Acceptance: Project builds an Android target with a minimal Compose entry point.
  - Acceptance: Source-set boundaries exist for common logic and Android-specific platform code.
  - Verification: Run Gradle help/build task for Android target and record result.

- [x] 1.2 Define project package and module conventions
  - Acceptance: Package naming, module naming, and source-set layout are documented in the repo.
  - Acceptance: No production feature logic is hidden in build scripts.
  - Verification: Review Gradle files and source tree.

- [x] 1.3 Add baseline CI-style local commands
  - Acceptance: Document commands for build, unit tests, Android tests if available, and formatting/linting.
  - Acceptance: Commands work locally or failed commands have documented blockers.
  - Verification: Run documented commands once.

## 2. Domain Model and State Core

- [x] 2.1 Implement sound and mix domain models with tests
  - Acceptance: Models cover Sound, SoundCategory, SoundLayer, SoundMix, PlaybackState, TimerState, and UserSettings.
  - Acceptance: Model invariants include stable ids, valid volume range, and no duplicate active layer ids.
  - Verification: Failing tests are written first, then pass.

- [x] 2.2 Implement reducer-style mix state transitions
  - Acceptance: Add, remove, mute, unmute, layer volume, master volume, favorite, save, delete, and recent-mix transitions are covered.
  - Acceptance: Reducers are deterministic and testable in common tests.
  - Verification: Unit tests cover each scenario in `mix-management`.

- [x] 2.3 Implement timer state transitions
  - Acceptance: Preset duration, custom duration, start, tick, fade window, cancel, extend, and completion transitions are covered.
  - Acceptance: Fade factor does not overwrite saved mix volume values.
  - Verification: Unit tests cover each scenario in `sleep-timer`.

## 3. Design System and Component Primitives

- [x] 3.1 Implement app theme tokens
  - Acceptance: Colors, typography, spacing, radius, and minimum touch target tokens exist in code.
  - Acceptance: Tokens map to the Chinese Stitch/Figma baseline.
  - Verification: Token tests or screenshot inspection confirm no default white Material surfaces in main screens.

- [x] 3.2 Implement reusable Compose primitives
  - Acceptance: PrimaryButton, IconButton, VolumeSlider, TimerPresetChip, SoundIcon, SettingsRow, ToggleRow, and SectionHeader exist.
  - Acceptance: Interactive primitives meet at least 44px touch targets.
  - Verification: Component preview or screenshot review checks touch target sizes and dark theme.

- [x] 3.3 Implement shell components
  - Acceptance: TopBar, BottomNav, MiniPlayer, and AppScaffold exist and can be used across MVP screens.
  - Acceptance: Scroll content never overlaps fixed Mini Player and BottomNav at 360x800, 390x844, and 430x932.
  - Verification: Screenshot inspection across target sizes.

## 4. Static MVP Screens

- [x] 4.1 Implement Home screen from visual baseline
  - Acceptance: Home includes now-playing hero, play action, favorite/timer/edit actions, volume, recent sounds, recommendations, and Mini Player.
  - Verification: Screenshot compared against `work/figma-cn/home.png`.

- [x] 4.2 Implement Mixer screen from visual baseline
  - Acceptance: Mixer includes editable mix title, three layer rows, add sound action, master volume, save mix, Mini Player, and BottomNav.
  - Verification: Screenshot compared against Chinese Stitch `_4/screen.png` or Figma if available.

- [x] 4.3 Implement Library screen from visual baseline
  - Acceptance: Library includes search, category chips, sound grid/list, active indicators, add/remove actions, Mini Player, and BottomNav.
  - Acceptance: Library does not render a white content background.
  - Verification: Screenshot compared against Chinese Stitch `_5/screen.png` with dark-background correction.

- [x] 4.4 Implement Timer screen from visual baseline
  - Acceptance: Timer includes duration display, presets, fade-out controls, end behavior, start/cancel/extend states, Mini Player, and BottomNav.
  - Verification: Screenshot compared against `work/figma-cn/timer.png`.

- [x] 4.5 Implement Saved Mixes screen from visual baseline
  - Acceptance: Saved Mixes includes filters, mix cards, favorite, edit/delete menu affordance, play button, create-new card, Mini Player, and BottomNav.
  - Acceptance: Header copy is `已保存` or `我的混音`, not ambiguous `库`.
  - Verification: Screenshot compared against Chinese Stitch `_3/screen.png` with dark-background correction.

- [x] 4.6 Implement Settings screen from visual baseline
  - Acceptance: Settings includes theme, audio quality, start-last-mix, offline download, privacy policy, restore purchases, Mini Player, and BottomNav.
  - Acceptance: Settings is normalized for 390px width and has no right-side white gutter.
  - Verification: Screenshot compared against `work/figma-cn/settings.png` with width correction.

## 5. Persistence and Catalog

- [x] 5.1 Implement local sound catalog
  - Acceptance: Core sounds include rain, thunder, ocean, forest, wind, fan, fireplace, white noise, brown noise, and pink noise.
  - Acceptance: Catalog supports category filtering and text search.
  - Verification: Unit tests cover filtering and search.

- [x] 5.2 Implement local persistence adapter
  - Acceptance: Saved mixes, favorites, recent mixes, current mix, timer defaults, and settings can be stored and restored.
  - Acceptance: Common code depends on repository interfaces, not platform storage APIs.
  - Verification: Repository tests use fake storage; Android storage smoke test is documented.

## 6. Android Playback Spike and Integration

- [x] 6.1 Implement Android playback spike behind PlaybackEngine
  - Acceptance: Android can play at least one bundled loopable sound through the playback engine boundary.
  - Acceptance: Common UI/domain code does not import Media3 or Android service APIs.
  - Verification: Manual playback test and Android-specific unit/instrumented test where feasible.

- [x] 6.2 Decide multi-layer playback strategy
  - Acceptance: Spike documents whether MVP will use multiple ExoPlayer instances, another Android audio strategy, or a constrained MVP fallback.
  - Acceptance: If fallback changes requirements, OpenSpec deviation is documented before implementation continues.
  - Verification: Record spike results in docs.

- [x] 6.3 Implement MediaSessionService background playback
  - Acceptance: Playback continues when app is backgrounded or screen is locked on supported Android devices.
  - Acceptance: System media controls can pause/resume where supported.
  - Verification: Manual Android device/emulator test with result notes.

- [x] 6.4 Integrate timer with playback fade/stop behavior
  - Acceptance: Active timer updates UI state and calls playback fade/stop behavior at the correct time.
  - Verification: Unit tests for timer state and manual playback fade/stop test.

## 7. Compliance and Platform Readiness

- [x] 7.1 Document Android permissions and reasons
  - Acceptance: Manifest permissions are documented with user-facing reason and MVP behavior mapping.
  - Verification: Manifest review.

- [x] 7.2 Add privacy/compliance placeholders
  - Acceptance: Settings has Privacy Policy and Restore Purchases entry points.
  - Acceptance: Docs list privacy policy, app filing, Huawei AppGallery, and Google dependency considerations.
  - Verification: Manual review of Settings and docs.

- [x] 7.3 Create HarmonyOS spike plan
  - Acceptance: A future spike plan defines ovCompose/Harmony build goals, success criteria, blockers, and rollback path to ArkTS/ArkUI.
  - Verification: Plan reviewed against ovCompose sample assumptions.

## 8. Final Verification

- [x] 8.1 Run full local verification
  - Acceptance: Build, unit tests, and available UI checks pass or have documented blockers.
  - Verification: Command outputs summarized in final task notes.

- [x] 8.2 Run visual verification
  - Acceptance: Screens render without white content backgrounds, major text overflow, or bottom control overlap.
  - Verification: Screenshots checked at 360x800, 390x844, and 430x932.

- [x] 8.3 Run code review
  - Acceptance: Review finds no blocking behavior bugs, spec deviations, or missing core tests.
  - Verification: Review findings documented before archive/apply.
