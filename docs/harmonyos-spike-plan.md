# HarmonyOS Spike Plan

HarmonyOS is not an MVP release gate. The first spike should prove whether Compose Multiplatform output can be packaged through an ovCompose-style Harmony project without blocking Android delivery.

## Goals

- Render one simple Compose Multiplatform screen in a Harmony app shell.
- Package a HAP successfully.
- Validate basic local audio playback.
- Identify whether background audio, notification controls, and timer stop/fade behavior are feasible.

## Success Criteria

- A dark themed screen using WhiteNoisePro tokens renders correctly.
- One local loopable sound can start and stop.
- Build instructions are reproducible from a clean checkout.
- Required Harmony-specific platform code is isolated from common domain/UI logic.

## Blockers to Watch

- ovCompose version compatibility with the selected Kotlin/Compose versions.
- HAP packaging changes across HarmonyOS versions.
- Background audio APIs and app review restrictions.
- Notification/media control support differences.
- Binary size and audio asset packaging.

## Rollback Path

If ovCompose is unstable or too costly, keep Android MVP on Compose Multiplatform and build a future HarmonyOS adapter with ArkTS/ArkUI. Reuse product specs, Chinese copy, assets, domain concepts, and test scenarios.
