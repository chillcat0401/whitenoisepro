# Tasks

## 1. 规格与测试红灯

- [x] 1.1 添加 catalog / sample content 红灯测试，锁定 8 个发布声音、新增搜索和默认混音
- [x] 1.2 添加 Android resource resolver 红灯测试，锁定新增 raw resource 映射

## 2. 生成音频与目录接入

- [x] 2.1 扩展音频生成脚本，生成 rain/ocean/forest/fireplace WAV 与 manifest
- [x] 2.2 更新 SoundCatalog、SampleContent 和 AndroidSoundResourceResolver
- [x] 2.3 运行音频 verify，确认 8 个资产通过 QA

## 3. UI icon 与视觉刷新

- [x] 3.1 增加 shared Compose icon primitives 和声音 icon 映射
- [x] 3.2 将 Home、Mixer、Library、Saved、Mini Player、BottomNav 的文字占位按钮替换为图标
- [x] 3.3 调整主题 token、卡片/按钮状态，使界面更现代且夜间友好

## 4. 验证

- [x] 4.1 运行 common / Android unit tests
- [x] 4.2 运行 Gradle build 或可用的 assemble/lint 验证
- [x] 4.3 更新任务状态并记录结果
