# Compliance Readiness

日期：2026-06-06

## Settings Entry Points

当前 release-ready Settings UI 已完成第一轮信任与占位项硬化：

- Privacy Policy：展示当前 MVP 隐私定位；发布前仍需接入公开可访问 URL。
- Restore Purchases：当前隐藏；接入 billing 前不展示。
- Theme：保留只读说明态，不承诺未实现切换。
- Audio Quality：保留只读说明态，不承诺未实现音质切换。
- Start Last Mix：已接入设置状态与持久化。
- Offline Downloads：改为禁用说明态；当前 MVP 使用本地打包声音，不提供独立下载管理。

## Before Google Play Release

- Publish a privacy policy URL.
- Document collected data. Current architecture does not add analytics, accounts, cloud sync, or ads.
- Keep `POST_NOTIFICATIONS` absent while the app only uses exempt MediaSession notifications.
- Confirm background playback disclosure in store listing if required.
- First-party generated noise assets are present with reproducible source and hashes; complete real-device subjective audio QA before closed testing.
- Complete physical-device audio QA.
- Prepare store screenshots from real app states.

## Before Mainland China / HarmonyOS Distribution

- Confirm whether the app requires ICP filing based on hosted policy/support URLs and app behavior.
- Prepare Chinese privacy policy and permissions disclosure.
- Review Huawei AppGallery requirements separately from Google Play.
- Avoid depending on Google Play Services in shared MVP logic.
- Keep audio assets licensed for Mainland China distribution.

## Paid Features

No billing SDK is integrated in the MVP architecture yet. Paid features and purchase restore must remain hidden until billing is specified, implemented, tested, and reviewed separately.
