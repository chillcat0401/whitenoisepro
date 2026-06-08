# Design: 扩展生成声音目录与现代化 UI

## Asset Strategy

本次不直接 fetch 第三方录音到 APK。继续使用第一方生成资产，原因是当前归档已经建立了可发布基线：生成脚本、固定 seed、manifest、哈希和 QA 全部在仓库内闭环。

新增 profiles：

- `rain`：宽带粉噪底层叠加高频细密脉冲感，用于“细雨”。
- `ocean`：低频慢起伏噪声和柔和宽带层，用于“远海”。
- `forest`：低电平自然底噪和稀疏高频纹理，用于“夜林”。
- `fireplace`：低频暖噪声和随机短促 crackle 纹理，用于“暖炉”。

所有声音仍输出 mono 44.1 kHz 16-bit PCM WAV，长度沿用现有周期长度。manifest 的 ownership 继续声明第一方生成，不含第三方录音或采样。

## Catalog

`SoundCatalog.all` 扩展到 8 个发布声音。新增声音使用稳定 id：

- `rain`
- `ocean`
- `forest`
- `fireplace`

分类：

- rain/ocean/forest 属于 `Nature`
- fireplace 属于 `Home`

`availableCategories` 继续从 catalog 计算。默认混音改为“深夜雨林”，包含棕噪、细雨、夜林和柔和风扇，避免首屏仍只展示噪声类。

## Android Resource Mapping

`AndroidSoundResourceResolver.resolve()` 增加新增 id 到 `R.raw.*_loop` 的映射。未知 id 仍 fallback 到 `brown_noise_loop`，保持历史 DataStore 兼容。

## Icon System

新增 `AppIcon` 和 `SoundIcon(iconKey, label, active)`。

图标不使用远程图片，不新增平台 drawable 依赖。shared Compose 使用 `Canvas` 绘制简单、低亮、可缩放的符号：

- play / pause / settings / timer / sliders / favorite / edit / delete / add / volume / mute
- bottom nav: home / mixer / library / timer / saved
- sound: noise / fan / rain / ocean / forest / fireplace

保留中文 label 用于屏幕可读文本和卡片标题，但图形承担第一视觉识别。

## Visual Refresh

主题保持暗色，但降低“纯黑 + 单一青色”的单调感：

- 背景使用更深的蓝黑。
- Surface 分层略提升对比。
- Primary 保持柔和青绿。
- Secondary 偏雾蓝。
- Tertiary 用暖金/火光色，服务 fireplace 和强调状态。
- 新增 subtle border 与 icon tint，减少纯色块占位感。

组件调整：

- Mini Player 使用图标播放按钮。
- BottomNav 显示 icon + label。
- Hero 主图标改为混音/当前声音图标，不再取首字。
- LayerRow、MixCard、Saved 操作使用图标。
- 新建混音和添加声音按钮保留明确文字，但可带 add icon。

## Testing

先写失败测试：

- common catalog test 期望 8 个发布 id、Nature 分类出现、中文搜索新增声音。
- common sample content test 期望默认混音引用新增发布声音。
- Android resolver test 期望新增 id 映射到新增 raw resource。

实现后运行：

- `./gradlew :composeApp:allTests`
- `node tools/generate_mvp_audio.mjs --verify`
- 可用时运行 `./gradlew lint assembleDebug`

