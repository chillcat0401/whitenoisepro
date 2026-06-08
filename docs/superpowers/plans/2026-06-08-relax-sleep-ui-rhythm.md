# 放松睡眠 UI 节奏执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `relax-sleep-ui-rhythm` 完成执行，以下步骤与 tasks.md 对齐。

**目标：** 在已现代化 UI 基础上降低视觉拥挤感，让 Home、Mixer、Library、Timer、Saved、Settings 更适合睡前低负担使用。

**范围：** 仅调整语义 spacing/dimens token、Scaffold 内容 padding、Mini Player / BottomNav 间距和主要页面布局节奏；不改音频播放、持久化、权限或数据模型。

## 执行清单

- [x] 添加 design token 红灯测试，锁定舒适边距、页面间距、卡片 padding 和底部控件高度
- [x] 添加 AppScaffold padding 红灯测试，锁定 content bottom padding 随 Mini Player / BottomNav 更新
- [x] 新增 comfort spacing / dimens token
- [x] 提取并接入 AppScaffold content padding 纯函数
- [x] 调整 Mini Player 与 BottomNav 的高度和底部间距
- [x] 放松 Home hero、最近使用和推荐声音节奏
- [x] 放松 Mixer layer row、按钮和主音量区域节奏
- [x] 放松 Library 搜索、分类和声音卡片节奏
- [x] 放松 Timer、Saved、Settings 的卡片和列表节奏
- [x] 运行目标单元测试
- [x] 运行 `testDebugUnitTest`
- [x] 运行 `lintDebug` 和 `assembleDebug`
- [x] 更新任务状态并确认 OpenSpec strict validation

## 验证记录

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleDebug`
- `openspec validate relax-sleep-ui-rhythm --strict`
