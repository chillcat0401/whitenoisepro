# release-readiness Spec Delta

## ADDED Requirements

### Requirement: Signed Android App Bundle

The Android release SHALL produce a signed Android App Bundle using a dedicated upload key whose secrets are excluded from version control.

#### Scenario: Release bundle is prepared

- **WHEN** the release owner runs the release bundle task with complete signing inputs
- **THEN** the project produces a signed AAB
- **AND** automated verification confirms the bundle structure and JAR signature

#### Scenario: Signing inputs are missing

- **WHEN** a release bundle task is requested without complete signing inputs
- **THEN** the build fails with an actionable signing configuration error
