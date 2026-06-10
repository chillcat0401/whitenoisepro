# MVP Audio Asset Acceptance and QA

## 当前状态

当前 Android build 已包含八个第一方程序化生成声音和 11 个 Freesound CC0 外部真实录音处理素材。

- 白噪声。
- 粉噪声。
- 棕噪声。
- 柔和风扇。
- 细雨。
- 远海。
- 夜林。
- 暖炉。

生成脚本：

`tools/generate_mvp_audio.mjs`

机器可读资产清单：

`docs/audio-assets/generated-audio-manifest.json`

外部真实录音发布清单：

`docs/audio-assets/external-release-audio-manifest.json`

第一方生成资产不含第三方录音或采样，使用固定 seed 和周期 inverse FFT 生成。外部真实录音均来自 Freesound Creative Commons 0，已记录来源、原始 hash、处理后 hash、处理步骤、机器 QA 和用户人工听测通过记录。closed testing 前仍必须完成真实设备播放、后台、锁屏和长时间 loop QA。

## 资产证据表

| 声音 | 文件 | 来源 | 授权状态 | 机器 QA | 人工 QA |
| --- | --- | --- | --- | --- | --- |
| 白噪声 | `white_noise_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 粉噪声 | `pink_noise_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 棕噪声 | `brown_noise_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 柔和风扇 | `fan_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 细雨 | `rain_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 远海 | `ocean_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 夜林 | `forest_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 暖炉 | `fireplace_loop.wav` | 第一方程序化生成 | 可用于 Google Play；中国大陆分发需发布前复核 | ready | needs-human-evidence |
| 软雨 | `rain_soft_loop.ogg` | Freesound CC0 `640655` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 轻屋顶雨 | `rain_light_roof_loop.ogg` | Freesound CC0 `669484` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 窗雨 | `rain_window_loop.ogg` | Freesound CC0 `669486` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 屋顶雨 | `rain_roof_loop.ogg` | Freesound CC0 `650428` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 柔和海浪 | `ocean_gentle_loop.ogg` | Freesound CC0 `417797` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 海浪 | `ocean_waves_loop.ogg` | Freesound CC0 `431853` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 海岸 | `ocean_shore_loop.ogg` | Freesound CC0 `278982` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 火焰噼啪 | `fire_crackle_loop.ogg` | Freesound CC0 `813328` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 炉火 | `fire_hearth_loop.ogg` | Freesound CC0 `836535` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 落地风扇 | `fan_floor_loop.ogg` | Freesound CC0 `843484` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |
| 林间风 | `wind_forest_loop.ogg` | Freesound CC0 `530908` | 可用于 Google Play；中国大陆分发需发布前复核 | ready | human-pass |

## 外部素材处理摘要

| 指标 | 当前值 |
| --- | --- |
| 外部发布素材数量 | 11 |
| 输出格式 | Ogg/Vorbis |
| Bitrate | 96k |
| Target loudness | 约 `-23 LUFS` |
| True peak target | `-4 dBFS` 处理目标，处理后均低于 `-2.6 dBFS` |
| 外部 processed 总大小 | 约 8.9 MB |
| Android raw 目录总大小 | 约 17 MB |
| 处理脚本 | `tools/promote_external_audio.mjs` |
| 原始 intake manifest | `work/audio-intake/intake-manifest.json` |
| 发布 manifest | `docs/audio-assets/external-release-audio-manifest.json` |

## 单个声音资产验收标准

每个发布声音必须记录：

- 文件名。
- 声音名称。
- 分类。
- 来源。
- 授权类型。
- 授权是否覆盖 Google Play。
- 授权是否覆盖中国大陆分发。
- 时长。
- 文件格式。
- 文件大小。
- loop 边界 QA 结果。
- 主观响度 QA 结果。

最低标准：

- loop 边界无明显 click、pop、突兀断点。
- 连续播放 10 分钟无明显疲劳或刺耳频段。
- 与其他 MVP 声音主观响度一致。
- 不含人声版权风险、音乐旋律版权风险或不明来源素材。
- 命名和分类能被用户理解。
- 包体大小不明显伤害安装转化。

## 默认混音验收标准

默认混音必须满足：

- 前 10 秒能体现产品价值。
- 不需要用户调参即可舒适播放。
- 至少 2-3 个声音层。
- master volume 默认不过响。
- timer 推荐值明确。

当前默认混音：

```text
深夜雨林
- 棕噪声
- 软雨
- 林间风
- 落地风扇
推荐 timer: 30 或 45 分钟
```

## 手动音频 QA 矩阵

| 场景 | 模拟器 | 真实设备 | 通过标准 |
| --- | --- | --- | --- |
| 单声音播放 10 分钟 | 可选 | 必需 | 无中断、无明显 loop click |
| 默认混音播放 10 分钟 | 可选 | 必需 | 多层声音稳定 |
| 调整单层音量 | 必需 | 必需 | 音量变化即时生效 |
| 调整 master volume | 必需 | 必需 | 所有层同步变化 |
| timer stop | 必需 | 必需 | 到点停止 |
| timer fade | 必需 | 必需 | 淡出平滑 |
| 切后台播放 | 必需 | 必需 | 不中断 |
| 锁屏播放 | 可选 | 必需 | 不中断 |
| 媒体键暂停 / 恢复 | 必需 | 必需 | 状态同步 |
| 蓝牙耳机控制 | 可选 | 必需 | 暂停 / 恢复有效 |
| 来电 / 其他音频打断 | 可选 | 必需 | audio focus 行为可接受 |

## closed testing 前 gate

- 至少一台真实 Android 设备完成 QA 矩阵。
- 至少一个默认混音通过。
- 程序化资产清单、外部发布素材清单、哈希和处理脚本与 APK 一致。
- 所有外部候选声音有授权记录、原始 hash、处理后 hash 和人工听测记录。
- 应用中不存在 `silence_loop.wav`。

## 人工 QA 记录模板

| 日期 | 设备 | Android 版本 | 输出设备 | 场景 | 结果 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 默认混音 10 分钟 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 锁屏后台播放 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 蓝牙媒体键 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 来电 / 其他音频打断 | [待补充：pass/fail] | [待补充] |

## 2026-06-10 机器接缝 QA(7 个新素材)

方法:解码 loop 文件,对比接缝两侧各 200ms 的 RMS 电平差(阈值 3dB)。

| soundId | 电平差 | 结论 |
|---|---|---|
| rain_thunder | 0.70 dB | PASS |
| wind_trees | 0.46 dB | PASS |
| stream_gentle | 2.08 dB | PASS |
| cafe_chatter | 0.19 dB(首切 7.58dB 撞人声峰,改 5-48s 重切) | PASS |
| crickets_night | 0.24 dB | PASS |
| airplane_cabin | 0.78 dB | PASS |
| train_ride | 0.98 dB | PASS |

机器 QA 不能替代真机人工听测(扬声器/耳机/低音量三档),后者完成前 manifest 中
releaseDecision 保持 pending-device-listening-qa。
