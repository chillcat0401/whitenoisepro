# mix-management Spec Delta

## MODIFIED Requirements

### Requirement: Saved mixes

The system SHALL allow users to save, rename, favorite, play, and delete mixes.

#### Scenario: User saves an unchanged mix twice

- **WHEN** the current mix has the same title, layer sound ids, layer volumes, muted states, and master volume as an existing saved mix
- **THEN** saving updates the existing saved mix timestamp instead of creating a duplicate row

#### Scenario: User saves a modified mix

- **WHEN** the current mix differs from existing saved mixes by title, layers, layer volume, muted state, or master volume
- **THEN** saving creates or upserts a distinct saved mix entry
