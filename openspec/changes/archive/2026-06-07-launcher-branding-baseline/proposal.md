# 启动图标与商店图标基线

## Why

当前 Manifest 没有应用图标，Android lint 报告 `MissingApplicationIcon`，无法形成完整的 internal/closed testing 安装体验，Google Play listing 也缺少必需的 512×512 图标资产。

## What Changes

- 添加深色背景、青绿色月牙与声波组成的无文字品牌标记。
- 添加 Android adaptive launcher icon 和 round icon。
- Manifest 设置 `android:icon` 与 `android:roundIcon`。
- 添加确定性商店图标生成脚本。
- 生成 512×512、32-bit PNG Google Play icon。
- 添加尺寸、格式和文件大小验证。

## Non-goals

- 最终品牌命名决策。
- 商标注册。
- feature graphic、截图或宣传图。

## Acceptance

- Android lint 不再报告 `MissingApplicationIcon`。
- launcher icon 使用 adaptive icon 资源。
- 商店图标为 512×512 PNG，文件小于 1024 KB。
- 图标不包含文字、价格、排名或 Google Play 标识。
- 完整构建和 OpenSpec strict validation 通过。
