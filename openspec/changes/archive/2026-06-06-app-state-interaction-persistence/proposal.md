# App 状态、交互与持久化闭环

## 为什么

WhiteNoisePro 当前已经有可运行的 Compose MVP 页面和 reducer/domain 基础，但多个页面仍是静态展示或 no-op 操作。第三方产品评审指出，生产级助眠应用需要让 playback、timer、persistence、restore 和 UI events 走一致的 state/effect 路径；release-readiness 文档也把 last mix 真实持久化列为未完成项。

如果继续在 `App.kt` 里直接维护 Compose state，后续保存混音、恢复上次混音、通知回调、定时器 tick 和 Settings 行为都会变得难以测试和维护。

## 用户故事

- 作为睡眠应用用户，我希望再次打开应用时能恢复上次的混音和常用设置，不需要每晚重新配置。
- 作为用户，我希望搜索、筛选、添加声音、调整音量、保存混音、播放已保存混音和定时器按钮是真实可用的。
- 作为开发者，我希望 UI 只发送明确 intent，业务状态和副作用集中在可测试的 commonMain 层。
- 作为后续上架执行者，我希望 repository、settings、timer 和 playback 的行为有单元测试支撑，避免 closed testing 中暴露明显假功能。

## 范围

本变更包含：

- 新增 commonMain 小型 `AppStore`。
- 定义 app-level intent，覆盖导航、播放、mix、library、timer、saved mixes 和 settings 核心操作。
- AppStore 启动时从 `AppRepository` restore `AppSnapshot`。
- AppStore 在核心状态变更后保存 `AppSnapshot`。
- UI 页面接收 callbacks，替换当前主要 no-op 控件。
- Library 搜索和分类筛选接入 `SoundCatalog.filter`。
- Settings 的 `启动时继续上次混音` toggle 接入 `UserSettings`。
- 更新 stale compliance 文档，使其不再声称 Restore Purchases 仍是生产 UI placeholder。
- 为 store 和 screen callback 边界补充 common unit tests。

## 非目标

本变更不包含：

- 真实声音资产替换。
- Android 13+ notification runtime permission 请求。
- 隐私政策 URL 打开或网页托管。
- Billing、恢复购买、订阅、广告或 analytics。
- 离线下载实现。
- HarmonyOS 打包。
- 完整 DI 框架或 Android ViewModel 重构。

## 功能验收

- AppStore restore 空 repository 时使用 `AppState()` 默认内容。
- AppStore restore 非空 snapshot 时恢复 saved mixes、recent mixes、current mix、timer defaults 和 settings。
- 保存当前混音会更新 saved mixes，并调用 repository save。
- 播放已保存混音会更新 current mix、recent mixes，并调用 playback engine play。
- 主音量和 layer 音量调整会更新 state，并保存 snapshot。
- Library 搜索和分类会影响显示列表。
- 添加声音会向当前混音添加新 layer，不产生重复 layer id。
- Timer preset/start/extend/cancel 会更新 timer state；开始播放时仍通过 playback engine。
- Settings `启动时继续上次混音` toggle 会更新 settings 并保存。
- UI 中当前主要 no-op callback 被替换为真实 intent 或明确保持只读。

## 技术验收

- `commonMain` 不依赖 Android、Media3、Google Play Services、billing 或 analytics。
- `AppStore` 依赖 `AppRepository`、`PlaybackEngine`、clock/id provider 等小接口，便于测试。
- 纯 reducer 仍保持在 domain 层，AppStore 不复制 reducer 内部逻辑。
- `WhiteNoiseProApp` 负责创建/remember store 并将 state/callback 下发给 screen。
- 单元测试先红后绿，覆盖核心 intent 和 persistence side effects。

## 验证

- 运行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest
```

- 运行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

- 如涉及明显 UI callback 调整，重新查看 360x800、390x844、430x932 截图，确认 Bottom Nav 和 Mini Player 不遮挡内容。

