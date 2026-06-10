# Five Day Google Play Closed Testing Launch

## Why

当前项目已有 signed AAB、基础模拟器 smoke、Google Play 文案草案和首批 Freesound CC0 种子素材。目标是在五天内把 WhiteNoisePro 推进到 Google Play closed testing 可提交状态，并优先积累原始测试用户。

本轮需要把 11 个已人工确认听感可用的 Freesound 种子素材晋升为发布资源，同时做轻量 UI 视觉升级、公开隐私政策页面、更新上架资料和 release QA 证据。

## What Changes

- 将 11 个 Freesound CC0 原始素材处理为 Android 发布音频资源，记录来源、处理步骤、hash、loop、loudness 和包体证据。
- 扩展 app 声音目录和 Android raw resource resolver，使真实素材可在 Library、Mixer、默认/保存混音中使用。
- 保留现有 8 个第一方程序化声音，避免旧 mix、测试和 fallback 失效。
- 新增 GitHub Pages 可托管的隐私政策静态页面，并保持 Play Console / Data safety / Settings 文案一致。
- 按新 UI 图到位后的方向做轻量皮肤升级：颜色、间距、图标、层级和关键页面密度，不重做导航架构。
- 更新 release-readiness 文档、store listing、closed testing checklist、QA 记录和 release candidate 证据。

## Scope

In scope:

- Android app 发布音频资源、catalog、resolver、默认内容和相关测试。
- 音频处理脚本、processed manifest、发布音频 QA 文档。
- 轻量 UI polish，不改变核心交互流。
- 静态隐私政策页面和 GitHub Pages 发布准备。
- signed release AAB、本地验证、模拟器 smoke、真机 QA 记录模板/证据。

Out of scope:

- Production release 直接上线。
- 新增账号、云同步、analytics、ads、billing、crash SDK。
- 完整按新 UI 图重做信息架构。
- Play Console 账号侧字段的虚构填充；无法确认的信息继续标记为 blocked owner input。

## Success Criteria

- 19 个声音资源可被 catalog 和 Android resolver 一一映射，未知 id 仍 fallback 到 brown noise。
- 11 个外部声音有 processed 文件、manifest、处理命令、原始/处理后 hash、loudness/loop/包体证据。
- release build 可构建 signed AAB，并通过 verifier、unit test、lint 和 release smoke。
- 隐私政策有可发布静态 HTML，Play worksheet 指向 GitHub Pages URL 占位/待替换字段。
- closed testing checklist 明确 ready、blocked、needs-human-evidence；若外部 Play Console 信息仍缺失，阻断项清晰。
