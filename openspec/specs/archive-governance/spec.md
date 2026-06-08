# archive-governance Specification

## Purpose
TBD - created by archiving change historical-archive-superpowers-acceptance. Update Purpose after archive.
## Requirements
### Requirement: Verifiable archive acceptance

The project SHALL retain a machine-readable acceptance record for every governed OpenSpec archive.

#### Scenario: A new change is ready to archive

- **WHEN** the archive acceptance gate runs in strict mode
- **THEN** all OpenSpec and Superpowers plan tasks are complete
- **AND** verification and code review evidence exist
- **AND** no unverifiable process claim or blocker remains

#### Scenario: A historical archive is retrospectively accepted

- **WHEN** a legacy archive cannot prove every historical TDD step
- **THEN** the acceptance record explicitly lists each unverifiable item
- **AND** current implementation verification is recorded separately from historical process evidence
- **AND** the record does not claim strict historical acceptance

### Requirement: Automated archive gate

The project SHALL provide an offline automated command that rejects incomplete or inconsistent
archive acceptance evidence.

#### Scenario: Required evidence is missing

- **WHEN** tasks, plan completion, review, verification evidence, or the acceptance record violates
  the selected mode
- **THEN** the command exits non-zero
- **AND** reports the specific missing or inconsistent evidence

#### Scenario: Project checks run

- **WHEN** the Gradle check lifecycle executes
- **THEN** the current strict archive acceptance gate also executes

