## ADDED Requirements

### Requirement: Five day closed testing release candidate

The project SHALL maintain a release candidate record for the five-day Google Play closed testing push.

#### Scenario: Release candidate is prepared

- **WHEN** the five-day closed testing release candidate is built
- **THEN** release-readiness docs MUST record AAB path, version, package name, SHA-256, package size, verification commands, emulator smoke result, and remaining Play Console blockers
- **AND** blocked external owner inputs MUST remain marked as blocked rather than ready

### Requirement: Public privacy policy page

The project SHALL provide a static privacy policy page suitable for GitHub Pages publication.

#### Scenario: Privacy policy is prepared

- **WHEN** the app is prepared for Google Play testing
- **THEN** the repository MUST include a static HTML privacy policy page that is public-URL-ready
- **AND** it MUST describe current local-only data behavior, permissions, audio source posture, and contact placeholders

### Requirement: External audio release manifest

The release candidate SHALL include machine-readable evidence for all bundled external audio.

#### Scenario: External audio is bundled

- **WHEN** an external audio resource is added to Android release resources
- **THEN** `docs/audio-assets/external-release-audio-manifest.json` MUST record original source evidence, original hash, processed hash, processing steps, loudness QA, loop QA, human listening QA, file size, and app sound id
