## ADDED Requirements

### Requirement: Candidate audio must not bypass release gates

The project SHALL keep internally generated candidate audio separate from publishable release assets until it passes the release audio intake process.

#### Scenario: Candidate audio is generated

- **WHEN** the internal audio asset studio generates candidate audio
- **THEN** the generated files MUST remain outside Android release resources by default
- **AND** they MUST be marked non-publishable until a future release change adds them to the catalog with manifest evidence and human listening QA

#### Scenario: Candidate is promoted to release asset

- **WHEN** a candidate audio file is promoted into the app release package
- **THEN** the release-readiness docs MUST include its source manifest, hash, generation parameters, machine QA metrics, and human listening QA status

