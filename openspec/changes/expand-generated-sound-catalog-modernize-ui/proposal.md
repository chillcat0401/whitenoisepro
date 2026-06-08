# 扩展生成声音目录与现代化 UI

## Why

基础白噪音能力已经可播放，但当前目录只有四个声音，缺少用户期望的雨声、海浪、森林、壁炉等助眠环境声。UI 也仍有较多文字占位按钮，缺少稳定 icon 表达，视觉层次偏素。

## What Changes

- 延续已归档的第一方确定性音频路线，新增四个可发布循环声音：
  - 细雨
  - 远海
  - 夜林
  - 暖炉
- 扩展 `SoundCatalog`、默认混音、保存混音和 Android raw resource 映射，确保目录只展示已打包资源。
- 更新音频生成脚本和 manifest，继续记录算法、seed、哈希、格式、RMS、峰值和 loop seam。
- 增加 shared Compose icon 系统，替换主要文字占位按钮和声音首字占位。
- 轻量刷新主题 token 与卡片/控制样式，使 UI 更现代但仍低亮、低打扰。

## Non-goals

- 不引入未经人工审核的第三方录音素材。
- 不接入在线素材服务、下载缓存、账户、订阅或素材商店。
- 不做大规模导航重构或新页面。
- 不承诺新增素材达到专业现场录音真实感。
- 不改变 Android 后台播放、MediaSession 或权限模型。

## User Stories

- 作为睡眠用户，我希望声音库里有雨、海、森林、炉火等常见环境声，这样不需要离开应用寻找素材。
- 作为准备发布的开发者，我希望每个声音都有可审计来源和 QA 指标，这样 closed testing 前能说明素材来源。
- 作为夜间使用者，我希望按钮有清晰图标，不需要读小字也能快速操作播放、定时、编辑、删除和收藏。
- 作为中文用户，我希望声音名称、分类和搜索能覆盖新增声音。

## Functional Scope

- Catalog 新增 4 个声音，并维护 stable id、中文名称、分类、描述、iconKey、loopAssetKey、defaultVolume。
- Android `res/raw` 新增 4 个 WAV，并由 resolver 按 stable id 映射。
- 默认与保存混音引用新增环境声，仍保持 2-4 层、不过响。
- Library 支持新增声音的中文名称、描述和 id 搜索。
- UI 使用 `AppIcon` / `SoundIcon` 显示声音和操作图标。

## UI/UX Acceptance Criteria

- Home、Mixer、Library、Saved、Mini Player 中的主要操作不再依赖单字文字占位表达。
- Bottom navigation 同时显示图标和文字，选中态清晰。
- SoundGridCard 用声音类型图标呈现不同 sound id，不再只取中文首字。
- 主题保持夜间友好，无大面积亮白，无高饱和刺眼背景。
- 触控目标维持至少 44dp。

## Technical Approach

- 在 `tools/generate_mvp_audio.mjs` 中扩展 asset profile。
- 使用同一 WAV QA 验证机制，不引入第三方音频下载。
- 更新 common tests 先锁定 catalog id、分类、搜索、默认混音和 iconKey。
- 更新 Android unit test 先锁定新增 id 到 raw resource 的映射。
- 在 `design` 包增加 lightweight icon primitives，用 Compose Canvas 绘制图形。
- 只调整现有页面组件，不新增路由。

## State Model

不新增持久化 schema。已有 snapshot 若包含旧 sound id 仍由 Android resolver 回退到 `brown_noise`。新增 catalog 数据进入 `SoundCatalog`，新增 mix 仅使用既有 `SoundMix` / `SoundLayer`。

## Platform Differences

- Android：新增 raw WAV 和 resource resolver 映射。
- common UI：新增 icon composable，可继续被未来 iOS/Desktop/HarmonyOS spike 复用。
- HarmonyOS：本次不做平台实现，仅保持 shared UI 与 catalog 数据可复用。

## Test Strategy

- common unit tests：catalog id、分类、search、metadata、默认混音引用可发布资源。
- Android unit tests：resource resolver 对新增 id 映射到对应 raw resource，未知 id 继续 fallback。
- Node tests：生成并验证 8 个 WAV 的格式、hash、RMS、peak、loop seam。
- Gradle verification：执行 common tests、Android unit tests 和 assemble/lint 可用范围。

## Acceptance

- 8 个发布声音均有 raw WAV、catalog entry、manifest 记录和 Android 映射。
- SoundCatalog 与 Android resolver 的 published ids 完全一致。
- 新增声音可被 Library 展示、分类和搜索。
- UI 主要控制和声音卡片使用图标表达。
- 相关测试、音频验证和 OpenSpec validation 通过。

