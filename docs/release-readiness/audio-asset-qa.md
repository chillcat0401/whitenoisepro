# MVP Audio Asset Acceptance and QA

## 当前状态

当前 Android build 已包含八个第一方程序化生成声音：

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

这些资产不含第三方录音或采样，使用固定 seed 和周期 inverse FFT 生成。closed testing 前仍必须完成真实设备主观试听和长时间 loop QA。

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
- 细雨
- 夜林
- 柔和风扇
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
- 程序化资产清单、哈希和生成脚本与 APK 一致。
- 所有后续第三方候选声音有授权记录。
- 应用中不存在 `silence_loop.wav`。

## 人工 QA 记录模板

| 日期 | 设备 | Android 版本 | 输出设备 | 场景 | 结果 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 默认混音 10 分钟 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 锁屏后台播放 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 蓝牙媒体键 | [待补充：pass/fail] | [待补充] |
| [待补充：YYYY-MM-DD] | [待补充：设备型号] | [待补充] | [待补充：扬声器/有线/蓝牙] | 来电 / 其他音频打断 | [待补充：pass/fail] | [待补充] |
