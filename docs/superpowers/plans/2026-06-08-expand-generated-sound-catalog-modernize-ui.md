# 扩展声音素材与现代化 UI 执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `expand-generated-sound-catalog-modernize-ui` 完成执行，以下步骤与 tasks.md 对齐。

**目标：** 扩展可发布本地声音目录，补齐 UI icon，并在夜间友好的前提下提升界面现代感。

**范围：** 仅覆盖本地生成音频、目录接入、Compose 本地图标、主题 token 和相关测试；不引入网络素材、第三方采样或新播放架构。

## 执行清单

- [x] 添加 catalog / sample content 红灯测试，锁定 8 个发布声音、新增搜索和默认混音
- [x] 添加 Android resource resolver 红灯测试，锁定新增 raw resource 映射
- [x] 扩展音频生成脚本，生成 rain/ocean/forest/fireplace WAV 与 manifest
- [x] 更新 SoundCatalog、SampleContent 和 AndroidSoundResourceResolver
- [x] 运行音频 verify，确认 8 个资产通过 QA
- [x] 增加 shared Compose icon primitives 和声音 icon 映射
- [x] 将 Home、Mixer、Library、Saved、Mini Player、BottomNav 的文字占位按钮替换为图标
- [x] 调整主题 token、卡片/按钮状态，使界面更现代且夜间友好
- [x] 运行 common / Android unit tests
- [x] 运行 Gradle build 或可用的 assemble/lint 验证
- [x] 更新任务状态并记录结果

## 验证记录

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleDebug`
- 音频资产 QA 验证覆盖 8 个发布声音资产
- `openspec validate expand-generated-sound-catalog-modernize-ui --strict`
