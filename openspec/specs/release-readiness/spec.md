# release-readiness Specification

## Purpose

定义 Google Play 测试发布、隐私合规、声音资产验收和平台分发准备的发布门槛。任何测试轨道或生产发布都必须依据真实代码行为、设备验证结果和可公开访问的政策材料完成检查。
## Requirements
### Requirement: Google Play release readiness

The project SHALL maintain a release-readiness checklist before attempting Google Play internal, closed, open, or production testing.

#### Scenario: Team prepares Google Play closed testing

- **WHEN** the team prepares a Google Play closed test
- **THEN** the checklist identifies tester requirements, feedback collection, store setup, privacy requirements, target API status, signed AAB status, foreground service declaration status, and exit criteria
- **AND** it marks each item as `ready`, `blocked`, or `needs-human-evidence`

#### Scenario: Catalog changes before release

- **WHEN** the bundled sound catalog changes
- **THEN** release readiness docs reflect the current number of published sounds, their first-party generation status, and remaining human listening QA

### Requirement: Five day closed testing release candidate

The project SHALL maintain a release candidate record for the five-day Google Play closed testing push.

#### Scenario: Release candidate is prepared

- **WHEN** the five-day closed testing release candidate is built
- **THEN** release-readiness docs MUST record AAB path, version, package name, SHA-256, package size, verification commands, emulator smoke result, and remaining Play Console blockers
- **AND** blocked external owner inputs MUST remain marked as blocked rather than ready

### Requirement: Privacy and data safety consistency

The project SHALL keep privacy policy content, Data safety answers, declared permissions, and actual code behavior consistent.

#### Scenario: Privacy policy template is prepared

- **WHEN** the project prepares for Google Play testing
- **THEN** the release docs include a privacy policy template covering developer identity, contact email, public URL, effective date, local data, permissions, data collection, data sharing, retention, deletion, security, and future SDK changes
- **AND** unconfirmed fields use explicit placeholders rather than invented values

#### Scenario: Developer information template is prepared

- **WHEN** the project prepares store listing and privacy materials
- **THEN** the release docs include a developer information template covering developer legal name or display name, support email, privacy contact email, region/address placeholder, website or support URL placeholder, privacy policy URL placeholder, and Play Console notes
- **AND** missing fields are marked as blocked owner inputs

#### Scenario: Privacy URL is missing

- **WHEN** the app is considered for Google Play testing
- **THEN** release readiness marks privacy policy publication as blocked until developer identity, contact email, effective date, and a public non-PDF URL are supplied

#### Scenario: App behavior changes data practices

- **WHEN** the app adds analytics, crash reporting, account features, billing, cloud sync, ads, or other SDKs that process user data
- **THEN** the privacy policy and Data safety worksheet are updated before release

#### Scenario: Data safety is reviewed

- **WHEN** code, dependencies, permissions, or local storage behavior changes
- **THEN** the Data safety worksheet is updated before release
- **AND** it states whether the current app collects or shares user data

### Requirement: Public privacy policy page

The project SHALL provide a static privacy policy page suitable for GitHub Pages publication.

#### Scenario: Privacy policy is prepared

- **WHEN** the app is prepared for Google Play testing
- **THEN** the repository MUST include a static HTML privacy policy page that is public-URL-ready
- **AND** it MUST describe current local-only data behavior, permissions, audio source posture, and contact placeholders

### Requirement: Notification permission rationale

The Android app SHALL explain that system media controls support background and lock-screen playback and are not marketing notifications.

#### Scenario: User reviews Settings

- **WHEN** the current MediaSession-only MVP displays its permission and privacy explanation
- **THEN** it explains background and lock-screen media control value
- **AND** it does not request `POST_NOTIFICATIONS`
- **AND** it does not imply marketing notifications

### Requirement: Publishable audio asset gate

The MVP SHALL not treat generated silence or placeholder audio as publishable product content.

#### Scenario: Candidate sound asset is reviewed

- **WHEN** a sound loop is considered for closed testing
- **THEN** it is checked for loop quality, loudness consistency, licensing, package-size impact, naming, and category fit

### Requirement: Candidate audio must not bypass release gates

The project SHALL keep internally generated candidate audio separate from publishable release assets until it passes the release audio intake process.

#### Scenario: Candidate audio is generated

- **WHEN** the internal audio asset studio generates candidate audio
- **THEN** the generated files MUST remain outside Android release resources by default
- **AND** they MUST be marked non-publishable until a future release change adds them to the catalog with manifest evidence and human listening QA

#### Scenario: Candidate is promoted to release asset

- **WHEN** a candidate audio file is promoted into the app release package
- **THEN** the release-readiness docs MUST include its source manifest, hash, generation parameters, machine QA metrics, and human listening QA status

### Requirement: Platform-neutral shared core

The shared/common source set SHALL not directly depend on Google-only services.

#### Scenario: Google SDK is introduced

- **WHEN** a future feature requires Google Play Services, billing, analytics, crash reporting, or review prompts
- **THEN** it is isolated behind a platform boundary with an explicit Huawei/HarmonyOS fallback decision

### Requirement: Reproducible first-party audio

Generated first-party audio used for testing or release SHALL be reproducible and documented.

#### Scenario: Generated audio is included in a build

- **WHEN** a generated WAV is packaged
- **THEN** the repository contains its generation script and fixed parameters
- **AND** an asset manifest records its hash and QA metrics
- **AND** the file is non-silent and loop-safe

#### Scenario: Release owner reviews audio assets

- **WHEN** a release candidate is prepared
- **THEN** the audio QA document lists all bundled published sounds
- **AND** every sound has generator metadata, hash evidence, and a human listening QA status

### Requirement: Release icon assets

The Android release SHALL include an adaptive launcher icon and a Google Play store icon.

#### Scenario: Android app is installed

- **WHEN** the launcher displays the app
- **THEN** Android renders the configured adaptive or round icon

#### Scenario: Store listing is prepared

- **WHEN** the Google Play listing icon is uploaded
- **THEN** the repository provides a 512 by 512 PNG smaller than 1024 KB
- **AND** the icon contains no misleading text or badges

### Requirement: Signed Android App Bundle

The Android release SHALL produce a signed Android App Bundle using a dedicated upload key whose secrets are excluded from version control.

#### Scenario: Release bundle is prepared

- **WHEN** the release owner runs the release bundle task with complete signing inputs
- **THEN** the project produces a signed AAB
- **AND** automated verification confirms the bundle structure and JAR signature

#### Scenario: Signing inputs are missing

- **WHEN** a release bundle task is requested without complete signing inputs
- **THEN** the build fails with an actionable signing configuration error

### Requirement: External audio release evidence

The project SHALL keep release evidence for any external audio before it is bundled in an app build.

#### Scenario: External audio is promoted

- **WHEN** an external audio candidate is promoted into Android release resources
- **THEN** release-readiness docs MUST include its source URL, license evidence, original hash, processed hash, processing steps, human listening QA, loop QA, loudness QA, and package-size impact

#### Scenario: BBC Sound Effects candidate is proposed

- **WHEN** a BBC Sound Effects recording is proposed for release
- **THEN** it MUST be rejected unless a paid commercial license from the authorized licensing path is recorded

### Requirement: External audio release manifest

The release candidate SHALL include machine-readable evidence for all bundled external audio.

#### Scenario: External audio is bundled

- **WHEN** an external audio resource is added to Android release resources
- **THEN** `docs/audio-assets/external-release-audio-manifest.json` MUST record original source evidence, original hash, processed hash, processing steps, loudness QA, loop QA, human listening QA, file size, and app sound id
