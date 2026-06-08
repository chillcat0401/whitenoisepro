# release-readiness Spec Delta

## MODIFIED Requirements

### Requirement: Google Play release readiness

The project SHALL maintain a release-readiness checklist before attempting Google Play internal, closed, open, or production testing.

#### Scenario: Team prepares Google Play closed testing

- **WHEN** the team prepares a Google Play closed test
- **THEN** the checklist identifies tester requirements, feedback collection, store setup, privacy requirements, target API status, signed AAB status, foreground service declaration status, and exit criteria
- **AND** it marks each item as `ready`, `blocked`, or `needs-human-evidence`

#### Scenario: Catalog changes before release

- **WHEN** the bundled sound catalog changes
- **THEN** release readiness docs reflect the current number of published sounds, their first-party generation status, and remaining human listening QA

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

#### Scenario: Data safety is reviewed

- **WHEN** code, dependencies, permissions, or local storage behavior changes
- **THEN** the Data safety worksheet is updated before release
- **AND** it states whether the current app collects or shares user data

### Requirement: Generated audio release evidence

Generated first-party audio used for testing or release SHALL be reproducible and documented.

#### Scenario: Release owner reviews audio assets

- **WHEN** a release candidate is prepared
- **THEN** the audio QA document lists all bundled published sounds
- **AND** every sound has generator metadata, hash evidence, and a human listening QA status
