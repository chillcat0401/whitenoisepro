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

#### Scenario: Store copy changes before release

- **WHEN** the app's brand copy, slogan, short description, or full description changes
- **THEN** release readiness docs MUST record policy-safe store listing copy that is consistent with current app behavior
- **AND** the copy MUST avoid medical claims, guaranteed sleep outcomes, analytics claims that conflict with code, and unconfirmed developer information
