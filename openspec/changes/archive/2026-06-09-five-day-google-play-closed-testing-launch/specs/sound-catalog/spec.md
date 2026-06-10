## MODIFIED Requirements

### Requirement: Published sound catalog integrity

The system SHALL expose only sound entries that have a bundled, publishable audio resource in the current release.

#### Scenario: Current release catalog is loaded

- **WHEN** the app loads the published sound catalog
- **THEN** it MUST include the existing first-party generated sounds
- **AND** it MUST include the 11 promoted Freesound CC0 external sounds
- **AND** every catalog sound id MUST resolve to a dedicated Android raw resource

### Requirement: Stable published sound identifiers

Each published sound SHALL have a stable identifier that maps to one Android audio resource.

#### Scenario: External sounds are promoted

- **WHEN** external Freesound sounds are bundled for closed testing
- **THEN** their ids MUST be stable snake_case values
- **AND** unknown or legacy ids MUST continue to fall back to a safe bundled brown noise resource
