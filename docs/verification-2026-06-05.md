# Verification 2026-06-05

## Passed

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

Result:

```text
BUILD SUCCESSFUL
```

This covers:

- Kotlin compilation for debug and release variants
- Android debug APK assembly
- Common/domain unit tests through Android unit test target
- Android lint
- Manifest/resource validation

## Figma MCP Baseline

Figma MCP can read the Chinese Stitch/Figma baseline:

```text
fileKey: vhk3qAl1ZDnmk8tOHDDqR4
page node: 0:1
Home node: 1:2
```

The Home design context confirms the MVP visual direction:

- 390px mobile width
- dark background `#111317`
- primary accent `#8AD3CE`
- persistent Mini Player
- compact bottom navigation
- Chinese Home/Timer/Settings content

## Android Emulator Verification

A local Android API 36 emulator was configured under:

```text
work/android-avd/WhiteNoisePro_API36.avd
```

The debug APK was installed and verified at 360 x 800, 390 x 844, and 430 x 932 mobile viewports:

```bash
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
adb shell wm density 160
```

Screenshots captured:

```text
work/android-screenshots/360x800/home.png
work/android-screenshots/360x800/mixer.png
work/android-screenshots/360x800/library.png
work/android-screenshots/360x800/timer.png
work/android-screenshots/360x800/saved.png
work/android-screenshots/360x800/settings.png
work/android-screenshots/360x800/home-scrolled.png
work/android-screenshots/home-390.png
work/android-screenshots/mixer-390.png
work/android-screenshots/library-390.png
work/android-screenshots/timer-390.png
work/android-screenshots/saved-390.png
work/android-screenshots/settings-390.png
work/android-screenshots/430x932/home.png
work/android-screenshots/430x932/mixer.png
work/android-screenshots/430x932/library.png
work/android-screenshots/430x932/timer.png
work/android-screenshots/430x932/saved.png
work/android-screenshots/430x932/settings.png
```

Result:

- Home, Mixer, Library, Timer, Saved Mixes, and Settings render at 360 x 800, 390 x 844, and 430 x 932.
- Bottom Nav labels fit without wrapping or overlap.
- Mini Player remains fixed and does not cover bottom navigation.
- Settings is normalized to 390px with no right-side white gutter.
- On 360 x 800, Home content that reaches the fixed Mini Player area can be scrolled to reveal the recommended sounds section.

## MediaSession Playback Verification

Manual emulator verification:

1. Start app.
2. Tap Home play.
3. Press Android Home key to background the app.
4. Inspect `dumpsys media_session`.
5. Send media pause/play key events.

Observed session state:

```text
state=PLAYING(3)
state=PAUSED(2)
state=PLAYING(3)
```

The service remained active while the app was backgrounded:

```text
com.whitenoisepro/.audio.WhiteNoiseMediaSessionService
```

## Current APK

```text
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```
