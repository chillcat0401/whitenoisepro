# Google Play 上架巡查与基础能力增强执行计划

> 计划用于归档验收证据。该变更按 OpenSpec `google-play-readiness-core-polish` 执行。

**目标：** 补齐 Google Play 上架巡查、隐私政策与开发者信息模板，并小范围增强首次入睡 timer、Settings 信任文案、保存混音去重和 Library 推荐入口。

**范围：** release-readiness 文档、Settings 内容模型、MixReducer 保存逻辑、AppStore 推荐 timer intent、Home / Library 轻量 UI 入口及对应测试。不新增第三方 SDK、账号、analytics、billing、广告、云同步或联网素材。

## 执行清单

- [x] 更新 Google Play checklist 到 8 声音、targetSdk 36、签名 AAB、closed testing、隐私 URL和真实设备 QA 状态
- [x] 新增 Google Play 上架巡查报告
- [x] 编写隐私政策模板和开发者信息模板，未知字段使用明确占位
- [x] 更新 Data safety、privacy policy draft、audio asset QA 和 closed testing 文档
- [x] 添加 SettingsContent 红灯测试
- [x] 添加 MixReducer / AppStore 保存去重红灯测试
- [x] 添加推荐入睡 timer 红灯测试
- [x] 更新 Settings release-ready 内容
- [x] 实现保存混音去重逻辑
- [x] 实现 Home 推荐入睡 timer 入口和 AppStore intent
- [x] 实现 Library 推荐入睡声音入口
- [x] 运行目标单元测试、完整 debug 单测、lint、debug assemble 和 OpenSpec strict validation

## 验证记录

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests com.whitenoisepro.presentation.SettingsContentTest --tests com.whitenoisepro.domain.MixReducerTest --tests com.whitenoisepro.presentation.AppStoreTest --tests com.whitenoisepro.app.AppShellTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleDebug`
- `openspec validate google-play-readiness-core-polish --strict`
