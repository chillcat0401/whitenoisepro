# release-readiness Spec Delta

## MODIFIED Requirements

### Requirement: Notification permission rationale

The Android app SHALL explain that system media controls support background and lock-screen playback and are not marketing notifications.

#### Scenario: User reviews Settings

- **WHEN** the current MediaSession-only MVP displays its permission and privacy explanation
- **THEN** it explains background and lock-screen media control value
- **AND** it does not request `POST_NOTIFICATIONS`
- **AND** it does not imply marketing notifications
