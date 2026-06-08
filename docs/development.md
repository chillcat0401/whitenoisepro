# Development Commands

Run from the project root.

## JDK

Android Gradle Plugin tasks require JDK 17:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

## Build

```bash
./gradlew :composeApp:assembleDebug
```

Builds the Android debug APK.

## Unit Tests

```bash
./gradlew :composeApp:testDebugUnitTest
```

Runs available Android debug unit tests for the current Android-first Compose Multiplatform target.

For the full local check pipeline, run:

```bash
./gradlew :composeApp:check
```

## Gradle Health

```bash
./gradlew help
```

Confirms the wrapper, plugin resolution, and project configuration can load.

## Context7 Docs Fallback

If Context7 MCP is unavailable, this machine needs the system CA bundle for direct CLI calls:

```bash
NODE_EXTRA_CA_CERTS=/etc/ssl/cert.pem ctx7 library <name> "<question>"
NODE_EXTRA_CA_CERTS=/etc/ssl/cert.pem ctx7 docs <libraryId> "<question>"
```

## Visual Verification Targets

Use these viewport sizes when checking Compose screenshots against the Chinese Figma/Stitch baseline:

```text
360x800
390x844
430x932
```
