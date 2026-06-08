# sound-catalog Spec Delta

## MODIFIED Requirements

### Requirement: Local sound catalog

The system SHALL expose only sound entries that have a bundled, publishable audio resource in the current release.

#### Scenario: User opens Library

- **WHEN** the user opens Library in the MVP release
- **THEN** the catalog shows white noise, pink noise, brown noise, fan, rain, ocean, forest, and fireplace
- **AND** it does not show sounds without corresponding owned or licensed bundled assets

### Requirement: Sound metadata

Each sound SHALL include stable id, localized display name, category, icon intent, loop asset reference, and default volume.

#### Scenario: UI renders a sound item

- **WHEN** a sound appears in Library or Mixer
- **THEN** the UI can read its display name, icon intent, category, active state, and volume defaults from catalog data
- **AND** each icon intent resolves to a local Compose-rendered icon without a network request

## ADDED Requirements

### Requirement: First-party generated ambient assets

Generated ambient assets SHALL be reproducible from repository scripts and SHALL not contain third-party recordings or samples.

#### Scenario: Audio asset QA runs

- **WHEN** the generated audio verification script runs
- **THEN** every published asset has a manifest entry with generator, seed/profile, SHA-256, duration, format, RMS, peak, and loop seam metrics

### Requirement: Expanded local search

The local catalog SHALL support Chinese search over all published ambient sounds.

#### Scenario: User searches ambient sounds

- **WHEN** the user searches for 雨, 海, 森林, or 炉
- **THEN** Library returns the matching bundled sound

