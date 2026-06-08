# First-run and Settings Placeholder Strategy

## First-run 默认体验

目标：

用户第一次打开应用后，不需要理解复杂功能，就能开始睡眠声音。

首屏应完成三件事：

1. 展示一个默认混音。
2. 提供一个主播放动作。
3. 给出一个推荐 timer。

默认路径：

```text
打开应用 -> 夜雨入眠 -> 播放 -> 30/45 分钟 timer 可见 -> 可后台播放
```

不做多页 onboarding，除非 onboarding 能直接帮助用户开始播放。

## 默认混音

名称：

```text
夜雨入眠
```

声音层：

- 雨声。
- 远雷。
- 棕噪声。

推荐 timer：

- 30 分钟或 45 分钟。

## Settings placeholder 策略

发布前，每个 placeholder 只能是以下状态之一：

- 实现：功能真实可用。
- 禁用：保留入口，但明确显示暂不可用或即将推出。
- 隐藏：不在 closed testing build 中展示。

## 当前 Settings 项决策

| 设置项 | 当前状态 | closed testing 前策略 |
| --- | --- | --- |
| 隐私政策 | 已硬化 | 当前显示无账号、无广告、偏好本地保存；发布前接公开 URL |
| 恢复购买 | 已隐藏 | 直到接入 billing 前不展示 |
| 离线下载 | 已禁用说明态 | 当前 MVP 使用本地打包声音，独立下载管理暂不可用 |
| 音质 | 已只读说明 | 当前为标准；真实音频质量档位确定前不开放选择 |
| 主题 | 已只读说明 | 当前为深色，睡前低亮度优先 |
| 启动时继续上次混音 | 已接入 | Android 使用 DataStore + Flow/suspend 保存和恢复 |

## 2026-06-06 第一轮 UI 硬化结果

已完成：

- `SettingsContent.releaseReady()` 作为可测试内容模型。
- `恢复购买` 从生产 Settings UI 中隐藏。
- `离线下载` 不再作为可切换假功能展示，改为暂不可用说明态。
- `隐私政策` 显示当前 MVP 隐私定位。
- `后台媒体控制` 说明 Android MediaSession 在播放时提供锁屏 / 通知栏控制，并明确不用于营销。

未完成：

- 公开隐私政策 URL。
- Android MediaSession 已确认不需要 notification runtime permission；未来新增普通通知时重新评估。
- 完成真实设备 force-stop/relaunch 的 DataStore smoke test。

## 信任定位

closed testing 文案应统一强调：

```text
无账号、无广告、偏好本地保存、后台播放只用于助眠声音控制。
```

该定位需要同步到：

- Settings。
- Google Play listing copy。
- 隐私政策。
- 权限说明。
