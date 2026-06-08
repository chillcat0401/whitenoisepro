# mix-management Specification

## Purpose

定义当前混音、声音层、收藏、保存混音和最近使用记录的状态转换规则。所有转换必须保持稳定标识、合法音量范围和无重复声音层，并能在 common tests 中确定性验证。

## Requirements
### Requirement: Current mix state
The system SHALL maintain a current mix containing zero or more sound layers, each with sound id, volume, and mute state.

#### Scenario: User adds a sound
- **WHEN** the user adds a sound from Library or Mixer
- **THEN** the sound appears as a layer in the current mix with default volume and unmuted state

#### Scenario: User removes a sound
- **WHEN** the user removes a sound layer
- **THEN** the sound no longer contributes to current mix playback or active Library state

### Requirement: Per-layer and master volume
The system SHALL support per-layer volume and master volume as separate state values.

#### Scenario: User changes layer volume
- **WHEN** the user drags a layer slider
- **THEN** only that sound layer volume changes

#### Scenario: User changes master volume
- **WHEN** the user drags the master volume slider
- **THEN** the effective playback output changes for all active layers without overwriting per-layer volume values

### Requirement: Saved mixes
The system SHALL let users save, edit, delete, favorite, and play named mixes.

#### Scenario: User saves current mix
- **WHEN** the user saves a mix with at least one layer
- **THEN** the mix appears in Saved Mixes with name, sound summary, favorite state, and play action

#### Scenario: User saves an unchanged mix twice

- **WHEN** the current mix has the same title, layer sound ids, layer volumes, muted states, and master volume as an existing saved mix
- **THEN** saving updates the existing saved mix timestamp instead of creating a duplicate row

#### Scenario: User saves a modified mix

- **WHEN** the current mix differs from existing saved mixes by title, layers, layer volume, muted state, or master volume
- **THEN** saving creates or upserts a distinct saved mix entry

#### Scenario: User plays saved mix
- **WHEN** the user taps play on a saved mix
- **THEN** the saved mix becomes the current mix and playback starts or is prepared according to playback state

### Requirement: Recent mixes
The system SHALL maintain recently used mixes for quick access on Home.

#### Scenario: User plays a mix
- **WHEN** a mix is played
- **THEN** the mix appears near the front of the recent mixes list without duplicate entries
