# Android Permissions

## Current Manifest

```text
android.permission.FOREGROUND_SERVICE
android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
```

## Reasons

`FOREGROUND_SERVICE`

Required for long-running playback work when the app is backgrounded.

`FOREGROUND_SERVICE_MEDIA_PLAYBACK`

Required on modern Android versions for foreground services whose type is media playback. This maps to the MVP requirement that sleep sounds continue while the screen is locked or the app is backgrounded.

`POST_NOTIFICATIONS` is intentionally not declared.

The app only publishes MediaSession notifications created by `MediaSessionService`. Android 13+ exempts media session notifications from the notification runtime permission behavior change, and foreground media playback does not require requesting `POST_NOTIFICATIONS`.

## Review Notes

- Common Kotlin code must not request or inspect Android permissions directly.
- Do not add a notification runtime prompt unless the product later adds non-exempt notifications.
- If the app store target changes permissions, update this file and the Manifest together.

Official references reviewed 2026-06-06:

- https://developer.android.com/guide/topics/ui/notifiers/notification-permission
- https://developer.android.com/media/media3/session/background-playback
