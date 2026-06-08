# release-readiness Specification

## Purpose

定义 Google Play 测试发布、隐私合规、声音资产验收和平台分发准备的发布门槛。任何测试轨道或生产发布都必须依据真实代码行为、设备验证结果和可公开访问的政策材料完成检查。
## Requirements
### Requirement: Google Play release readiness

The project SHALL maintain a release-readiness checklist before attempting Google Play closed testing or production access.

#### Scenario: Preparing closed testing

- **WHEN** the team prepares a Google Play closed test
- **THEN** the checklist identifies tester requirements, feedback collection, store setup, privacy requirements, and exit criteria

### Requirement: Privacy and Data safety readiness

The project SHALL keep privacy policy content, Data safety answers, declared permissions, and actual code behavior consistent.

#### Scenario: App behavior changes data practices

- **WHEN** the app adds analytics, crash reporting, account features, billing, cloud sync, ads, or other SDKs that process user data
- **THEN** the privacy policy and Data safety worksheet are updated before release

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

