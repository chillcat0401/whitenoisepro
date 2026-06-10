# settings-trust Specification

## Purpose

定义 Settings 中面向用户的隐私、后台媒体控制和未开放能力说明，避免展示不可用的伪功能。文案必须准确反映本地存储、无账号和媒体通知行为，并为 closed testing 提供可理解的信任信息。
## Requirements
### Requirement: Release-ready Settings trust copy

The Settings screen SHALL display privacy, local-data, audio-source, and background media-control rationale suitable for closed testing.

#### Scenario: User reviews privacy posture

- **WHEN** the user opens Settings in a release candidate
- **THEN** Settings communicates that the MVP has no account, no ads, no analytics, and local-only preferences
- **AND** it does not imply a published privacy policy URL or finalized developer identity exists until one is configured

#### Scenario: User reviews audio source posture

- **WHEN** the user opens Settings in a release candidate
- **THEN** Settings communicates that current sounds are locally bundled audio, including first-party generated noise and processed Creative Commons 0 natural recordings
- **AND** it does not claim medical, therapeutic, or guaranteed sleep outcomes

#### Scenario: User reviews background controls

- **WHEN** the user opens Settings in a release candidate
- **THEN** Settings explains that Android system media controls are for background and lock-screen playback
- **AND** it does not describe marketing notifications or request notification runtime permission

### Requirement: Placeholder functionality must not appear available

The Settings screen SHALL hide or disable features that are not implemented for the current MVP.

#### Scenario: Billing is not integrated

- **WHEN** billing is not integrated
- **THEN** Restore Purchases is not shown as an available Settings row

#### Scenario: Offline downloads are not implemented

- **WHEN** offline downloads are not implemented
- **THEN** Offline Downloads is shown as unavailable or hidden, not as an enabled toggle

