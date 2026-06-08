## ADDED Requirements

### Requirement: Persisted settings
The system SHALL persist user settings including theme mode, audio quality, start-last-mix behavior, offline download preference, and notification control preference.

#### Scenario: User changes start-last-mix
- **WHEN** the user toggles start-last-mix
- **THEN** the choice is persisted and restored on next launch

### Requirement: Privacy and restore purchase entry points
The system SHALL provide Settings entries for Privacy Policy and Restore Purchases.

#### Scenario: User opens Settings
- **WHEN** Settings is displayed
- **THEN** Privacy Policy and Restore Purchases actions are visible

### Requirement: Minimum permissions posture
The Android MVP SHALL request only permissions required for audio playback, foreground media service, notifications where applicable, and local app behavior.

#### Scenario: Permission review
- **WHEN** the Android manifest is reviewed
- **THEN** every declared permission has a documented reason tied to MVP behavior

### Requirement: Mainland China and Huawei readiness notes
The system SHALL keep Google Play specific services optional and document requirements for Huawei AppGallery, HarmonyOS, privacy policy, and app filing readiness.

#### Scenario: Huawei readiness review
- **WHEN** the project is reviewed for Huawei or China Mainland distribution
- **THEN** the architecture identifies Google-dependent services, privacy policy needs, filing needs, and whether HarmonyOS requires ovCompose or ArkTS/ArkUI work
