# sleep-timer Spec Delta

## MODIFIED Requirements

### Requirement: Sleep timer configuration

The system SHALL let users choose preset or custom sleep timer durations before starting playback stop behavior.

#### Scenario: First-time user starts a bedtime session

- **WHEN** the user opens Home or Timer before starting playback
- **THEN** the UI presents a recommended 30 or 45 minute sleep timer path
- **AND** the user can start the timer without navigating through unrelated settings

#### Scenario: User starts recommended timer from Home

- **WHEN** the user selects the recommended bedtime timer action from Home
- **THEN** the app prepares the selected duration
- **AND** starts playback if playback is not already active
- **AND** shows remaining timer state in the Mini Player
