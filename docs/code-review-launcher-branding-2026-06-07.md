# Code Review: 启动图标与商店图标基线

日期：2026-06-07

## Findings

未发现阻塞本次变更的问题。

审查期间修复：

- adaptive icon 曾被移动到无版本限定目录，AAPT 无法生成资源；已恢复到 `mipmap-anydpi-v26`。
- 初版缺少 Android 13 themed icon 所需的 monochrome 图层；已补齐。

## Spec Alignment

- Manifest 同时声明 adaptive launcher icon 和 round icon。
- foreground、monochrome 与商店图标使用一致的月牙和声波标记。
- 图标不包含文字、价格、排名或 Google Play 标识。
- 商店图标由纯 Node 脚本确定性生成，不依赖本机图形工具。

## Verification

- `node --test tools/generate_store_icon.test.mjs`：1 test，0 failures。
- 两次临时生成输出逐字节一致。
- `file` 与 `sips`：512×512、8-bit RGBA PNG。
- 文件大小：6723 bytes，小于 1024 KB。
- `:composeApp:check :composeApp:assembleDebug`：`BUILD SUCCESSFUL`。
- Kotlin/Android tests：126 tests，0 failures，0 errors。
- APK 包含 launcher foreground、adaptive icon 和 round icon 资源。
- lint 不再报告 `MissingApplicationIcon` 或 `MonochromeLauncherIcon`。

## Remaining Risks

- 仍需在至少一台 Android 设备上检查圆形、圆角矩形和 themed icon 遮罩效果。
- 当前图标是 MVP 品牌基线，最终品牌命名和商标检索不在本变更范围内。
- Google Play feature graphic 和真实应用截图仍未准备。

Lint 剩余 warning：

- `activity-compose` 有稳定版更新提示。
- `mipmap-anydpi-v26` 与 minSdk 26 重复，但 adaptive icon 不能下移到无版本限定目录。
- 既有 `Uri.parse` KTX 建议。
