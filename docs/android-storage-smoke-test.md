# Android DataStore Storage Smoke Test

日期：2026-06-06

Android production persistence now uses:

- AndroidX Preferences DataStore `1.2.1`.
- Kotlin coroutines and Flow.
- A versioned JSON snapshot stored under `app_snapshot_json`.
- `LocalAppRepository` with Flow/suspend interfaces.
- `AndroidDataStoreAppStorage` in `androidMain`.

Automated tests cover codec round-trip, malformed JSON fallback, repository emissions, async AppStore restore/save, and DataStore adapter read/write.

Run this manual process-restart smoke test on a device or emulator:

1. Launch the debug app.
2. Save a mix.
3. Favorite the mix.
4. Change timer defaults.
5. Toggle `启动时继续上次混音`.
6. Force stop the app from system settings or use:

```bash
adb shell am force-stop com.whitenoisepro
```

7. Relaunch the app.
8. Confirm saved mix, favorite state, timer defaults, recent mixes, and settings are restored.
9. With `启动时继续上次混音` enabled, confirm current mix is restored.
10. Disable it, force stop again, and confirm the default current mix is used while saved mixes/settings remain.

DataStore files remain private app storage and must not be treated as user-exportable backups.

The Android adapter remains under:

```text
composeApp/src/androidMain/kotlin/com/whitenoisepro/storage/
```

Common code must continue to depend only on repository/storage interfaces.
