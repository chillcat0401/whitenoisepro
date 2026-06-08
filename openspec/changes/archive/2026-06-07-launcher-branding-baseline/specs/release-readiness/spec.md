# release-readiness Spec Delta

## ADDED Requirements

### Requirement: Release icon assets

The Android release SHALL include an adaptive launcher icon and a Google Play store icon.

#### Scenario: Android app is installed

- **WHEN** the launcher displays the app
- **THEN** Android renders the configured adaptive or round icon

#### Scenario: Store listing is prepared

- **WHEN** the Google Play listing icon is uploaded
- **THEN** the repository provides a 512 by 512 PNG smaller than 1024 KB
- **AND** the icon contains no misleading text or badges
