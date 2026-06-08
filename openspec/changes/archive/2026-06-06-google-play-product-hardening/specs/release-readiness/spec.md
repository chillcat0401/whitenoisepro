## ADDED Requirements

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

The Android app SHALL explain notification permission in the context of background and lock-screen playback controls.

#### Scenario: User sees notification permission copy

- **WHEN** Android 13+ notification permission is requested
- **THEN** the copy explains media control value and does not imply marketing notifications

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
