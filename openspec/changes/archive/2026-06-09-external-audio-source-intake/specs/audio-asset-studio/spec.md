## ADDED Requirements

### Requirement: External audio source intake

The project SHALL maintain an external audio intake process for seed recordings when internal procedural generation does not meet product quality.

#### Scenario: External seed candidate is selected

- **WHEN** an external audio file is considered as a seed candidate
- **THEN** the intake record MUST include source URL, source channel, author or provider, license, commercial-use status, download status, original filename, and intended app category
- **AND** the candidate MUST remain outside Android release resources until a future release intake change promotes it

#### Scenario: Source license is not release-safe

- **WHEN** a source is non-commercial, unclear, or requires a paid commercial license
- **THEN** the candidate MUST be marked blocked for release until license evidence is supplied
