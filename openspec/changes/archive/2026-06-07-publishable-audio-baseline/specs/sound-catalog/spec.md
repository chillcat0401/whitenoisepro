# sound-catalog Spec Delta

## MODIFIED Requirements

### Requirement: Local sound catalog

The system SHALL expose only sound entries that have a bundled, publishable audio resource in the current release.

#### Scenario: User opens Library

- **WHEN** the user opens Library in the MVP release
- **THEN** the catalog shows white noise, pink noise, brown noise, and fan
- **AND** it does not show nature sounds without corresponding licensed assets

## ADDED Requirements

### Requirement: Stable sound identifiers

Each published sound SHALL have a stable identifier that maps to one Android audio resource.

#### Scenario: Android plays a sound layer

- **WHEN** a mix layer references a published sound identifier
- **THEN** Android resolves and loops the corresponding raw resource

#### Scenario: Android restores an unknown legacy sound

- **WHEN** a restored layer contains an identifier not published in the current build
- **THEN** Android falls back to the brown-noise resource without crashing
