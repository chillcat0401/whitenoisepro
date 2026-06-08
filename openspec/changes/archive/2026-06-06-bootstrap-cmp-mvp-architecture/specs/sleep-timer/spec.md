## ADDED Requirements

### Requirement: Timer duration selection
The system SHALL support timer presets and custom timer duration.

#### Scenario: User selects preset
- **WHEN** the user selects 15 minutes, 30 minutes, 45 minutes, 1 hour, or 2 hours
- **THEN** the timer duration updates to the selected preset

### Requirement: Active timer countdown
The system SHALL track active timer countdown and expose remaining time to Home, Timer, and Mini Player.

#### Scenario: Timer is active
- **WHEN** the timer is running
- **THEN** the UI displays remaining time in the Timer screen and Mini Player

### Requirement: Fade-out
The system SHALL support an optional fade-out period before the timer ends.

#### Scenario: Fade-out enabled
- **WHEN** fade-out is enabled and the timer enters the fade-out window
- **THEN** playback volume decreases toward silence without changing saved mix volume values

### Requirement: Timer end behavior
The system SHALL stop playback when the timer ends if the user selected stop playback behavior.

#### Scenario: Timer reaches zero
- **WHEN** the active timer reaches zero
- **THEN** playback stops and timer state becomes inactive

### Requirement: Timer cancellation and extension
The system SHALL allow users to cancel or extend an active timer.

#### Scenario: User extends timer
- **WHEN** the user taps an extend action
- **THEN** the active timer remaining duration increases by the selected extension amount
