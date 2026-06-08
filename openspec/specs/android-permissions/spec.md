# android-permissions Specification

## Purpose

限定 Android 版本只声明后台媒体播放实际需要的权限，并确保用户侧权限说明与 Manifest 行为一致。

## Requirements
### Requirement: Minimum Android permissions

The Android app SHALL declare only permissions required for MediaSession background playback.

#### Scenario: App uses only MediaSession notifications

- **WHEN** the release manifest is built
- **THEN** it declares foreground service media playback permissions
- **AND** it does not declare `POST_NOTIFICATIONS`
- **AND** the app does not show a notification runtime permission prompt
