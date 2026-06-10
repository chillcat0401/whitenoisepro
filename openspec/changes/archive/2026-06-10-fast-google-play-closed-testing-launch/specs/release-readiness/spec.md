## ADDED Requirements

### Requirement: Fast Google Play closed testing launch gate

The project SHALL maintain a pre-closed-testing launch gate before submitting a Google Play closed testing release.

#### Scenario: Release owner prepares closed testing

- **WHEN** the release owner prepares a Google Play closed testing release
- **THEN** the launch gate MUST record the status of privacy policy URL, developer identity, support email, privacy contact email, Data safety worksheet, store listing copy, screenshots, signed AAB verification, Play App Signing status, target API status, permission review, release candidate smoke QA, real-device audio QA, tester roster, and feedback collection channel
- **AND** each item MUST be marked `ready`, `blocked`, or `needs-human-evidence`

#### Scenario: Required external input is missing

- **WHEN** privacy URL, developer identity, signing readiness, Play Console status, tester roster, or real-device QA evidence is missing
- **THEN** the closed testing launch gate MUST remain blocked
- **AND** the missing item MUST use an explicit owner-input placeholder rather than inferred or invented values

### Requirement: Release candidate package for closed testing

The project SHALL provide a release candidate package that can be uploaded to Google Play internal or closed testing.

#### Scenario: Release candidate is prepared

- **WHEN** a release candidate is prepared for Play Console upload
- **THEN** the package MUST include a signed Android App Bundle, bundle verification evidence, current targetSdk evidence, permission review evidence, store icon evidence, screenshot inventory, privacy policy URL status, Data safety status, and known release notes
- **AND** the release notes MUST avoid medical, therapeutic, or guaranteed sleep-improvement claims

#### Scenario: Build verification fails

- **WHEN** the release candidate fails build, signing, bundle verification, lint, unit tests, or smoke QA
- **THEN** the candidate MUST NOT be submitted to closed testing
- **AND** the failure MUST be recorded as a blocking release-readiness item

### Requirement: Internal testing before closed testing

The project SHALL use internal testing or equivalent pre-closed-testing validation before opening the closed testing track.

#### Scenario: Internal smoke test is completed

- **WHEN** the signed release candidate is installed through internal testing or equivalent local verification
- **THEN** Home playback, Library browsing, Mixer basics, Timer setup, Saved mixes, Settings trust copy, background playback, media controls, and no-crash log checks MUST be verified
- **AND** the verification record MUST include device or emulator identity, Android version, package version, and date

#### Scenario: P0 or P1 issue is found

- **WHEN** internal testing finds a P0 or P1 issue
- **THEN** closed testing submission MUST pause until the issue is fixed or explicitly accepted with rationale

