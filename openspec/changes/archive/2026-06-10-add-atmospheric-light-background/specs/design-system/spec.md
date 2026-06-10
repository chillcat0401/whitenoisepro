## MODIFIED Requirements

### Requirement: Relaxed release-ready visual rhythm
The app SHALL present a calm, modern, low-distraction visual rhythm suitable for sleep and focus use.

#### Scenario: Lightweight UI polish is applied
- **WHEN** the five-day closed testing release candidate is prepared
- **THEN** UI polish MUST improve spacing, tonal contrast, icon coverage, and key card hierarchy
- **AND** it MUST NOT change the existing tab navigation or core workflows
- **AND** all on-screen text MUST remain readable without overlap on supported phone viewports

#### Scenario: Atmospheric background is applied
- **WHEN** the app renders a primary screen
- **THEN** the root background MUST include subtle low-contrast light and shadow treatment inspired by the provided cropped reference images
- **AND** the treatment MUST remain local, non-interactive, and independent of network or remote image assets
- **AND** the treatment MUST NOT reduce text and control readability

#### Scenario: Content respects status bar breathing room
- **WHEN** the app renders top-level content below the Android status bar
- **THEN** the content top padding MUST provide more breathing room than the previous release candidate
- **AND** titles and top actions MUST NOT appear visually cramped against the status bar
