# settings-compliance Spec Delta

## MODIFIED Requirements

### Requirement: Persisted settings

The system SHALL persist user settings including theme mode, audio quality, start-last-mix behavior, offline download preference, and haptics preference.

#### Scenario: User changes start-last-mix

- **WHEN** the user toggles start-last-mix
- **THEN** the choice is persisted and restored on next launch

### Requirement: Privacy and restore purchase entry points

The system SHALL provide a Settings entry for Privacy Policy and SHALL only show Restore Purchases when billing is integrated.

#### Scenario: Billing is not integrated

- **WHEN** Settings is displayed in the current MVP
- **THEN** Privacy Policy is visible
- **AND** Restore Purchases is hidden

#### Scenario: Billing is integrated in a future release

- **WHEN** the release supports purchases
- **THEN** Restore Purchases is available and performs a real billing restore action
