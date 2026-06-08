# sound-catalog Specification

## Purpose

定义本地声音目录的分类、搜索、稳定标识和无网络可发现性要求。目录元数据必须支持睡眠相关分类和中文搜索，并在离线状态下为 Library 与 Mixer 提供一致的数据来源。
## Requirements
### Requirement: Local sound catalog

The system SHALL expose only sound entries that have a bundled, publishable audio resource in the current release.

#### Scenario: User opens Library

- **WHEN** the user opens Library in the MVP release
- **THEN** the catalog shows white noise, pink noise, brown noise, and fan
- **AND** it does not show nature sounds without corresponding licensed assets

### Requirement: Sound metadata
Each sound SHALL include stable id, localized display name, category, icon intent, loop asset reference, and default volume.

#### Scenario: UI renders a sound item
- **WHEN** a sound appears in Library or Mixer
- **THEN** the UI can read its display name, icon intent, category, active state, and volume defaults from catalog data

### Requirement: Offline-first audio assets
The MVP SHALL assume bundled or locally available loopable sound assets for core sounds.

#### Scenario: Device is offline
- **WHEN** the user opens the MVP without network access
- **THEN** the core catalog and bundled sounds remain usable

### Requirement: Catalog search and filtering
The system SHALL support text search and category filtering over local sound metadata.

#### Scenario: User searches white noise
- **WHEN** the user enters a search term matching sound name or category
- **THEN** Library displays matching sounds and preserves active mix indicators

### Requirement: Stable sound identifiers

Each published sound SHALL have a stable identifier that maps to one Android audio resource.

#### Scenario: Android plays a sound layer

- **WHEN** a mix layer references a published sound identifier
- **THEN** Android resolves and loops the corresponding raw resource

#### Scenario: Android restores an unknown legacy sound

- **WHEN** a restored layer contains an identifier not published in the current build
- **THEN** Android falls back to the brown-noise resource without crashing

