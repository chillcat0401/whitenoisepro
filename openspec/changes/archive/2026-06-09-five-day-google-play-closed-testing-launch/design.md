# Design

## Audio Promotion

新增本地发布音频处理脚本，输入 `work/audio-intake/intake-manifest.json` 和原始 WAV，输出：

- processed Ogg/Vorbis 发布资源到 Android raw 资源目录。
- `docs/audio-assets/external-release-audio-manifest.json`，记录每条外部素材的 source URL、author、license、original SHA-256、processed SHA-256、处理命令、duration、loudness、peak、loop QA 和包体大小。
- 机器 QA 失败时脚本退出非 0，不更新发布 manifest。

处理策略：

- 原始音频不直接入包。
- 每条外部素材导出 60-120 秒发布片段；短素材保留主体长度。
- 所有片段做淡入淡出，目标响度约 `-23 LUFS`，保留峰值余量。
- 输出文件名使用稳定 snake_case，对应 app sound id。
- 包体目标：新增外部音频后 AAB 尽量保持在可接受范围，若明显超大则优先降 bitrate 而不是删声音。

## App Catalog

保留现有 8 个声音 id，新增 11 个外部声音 id：

- `rain_soft`
- `rain_light_roof`
- `rain_window`
- `rain_roof`
- `ocean_gentle`
- `ocean_waves`
- `ocean_shore`
- `fire_crackle`
- `fire_hearth`
- `fan_floor`
- `wind_forest`

`SoundCatalog` 中新增名称、分类、描述和默认音量。`AndroidSoundResourceResolver` 对 19 个 id 映射到 dedicated raw resource，未知 id fallback 到 `brown_noise_loop`。

默认 mix 更新为真实素材优先，但保留噪声底层：

- `brown_noise`
- `rain_soft`
- `wind_forest`
- `fan_floor`

保存混音更新为包含海浪、火炉、风扇真实素材，保证 closed testing 首屏能体验真实录音。

## UI Polish

本轮只做轻量皮肤升级：

- 更新 design tokens 的背景、surface、accent、spacing 和圆角，让界面更松弛。
- 更新现有 icon key 映射，保证新增声音有图标。
- Home、Library、Mixer、Timer、Saved、Settings 保持导航和功能不变，只调整密度、卡片层级和重点控件样式。
- 新 UI 图到达后按同一边界套用，不引入新页面架构。

## Privacy and Play Readiness

新增 `site/privacy-policy.html`，内容基于当前隐私草案：

- 无账号、无广告、无 analytics、无 cloud sync、无数据分享。
- 本地偏好保存在设备上。
- 音频为本地打包资源。
- 使用 foreground media playback 权限解释。
- 未确认开发者信息保留占位，不虚构。

release docs 同步：

- store listing 更新为 19 个本地声音，包含外部 CC0 素材说明。
- Play worksheet 指出 GitHub Pages URL 待部署后替换。
- release candidate 记录新 AAB hash、包体、QA 和剩余 Play Console 阻断项。

## Verification

执行层必须：

- 先写或更新测试来覆盖 catalog/resolver/manifest 预期。
- 音频处理脚本可重复运行且输出稳定 manifest。
- 构建和验证命令写入 release docs。
- 无法完成的账号侧 Play Console 项保持 blocked owner input，不伪造 ready。
