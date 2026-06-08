# android-permissions Spec Delta

## ADDED Requirements

### Requirement: Minimum Android permissions

The Android app SHALL declare only permissions required for MediaSession background playback.

#### Scenario: App uses only MediaSession notifications

- **WHEN** the release manifest is built
- **THEN** it declares foreground service media playback permissions
- **AND** it does not declare `POST_NOTIFICATIONS`
- **AND** the app does not show a notification runtime permission prompt
