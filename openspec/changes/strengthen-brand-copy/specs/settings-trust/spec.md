## MODIFIED Requirements

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
