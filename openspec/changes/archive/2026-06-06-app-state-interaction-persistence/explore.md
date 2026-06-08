# Explore: App 状态、交互与持久化闭环

日期：2026-06-06

## 背景

当前 WhiteNoisePro 已完成 Compose Multiplatform Android-first MVP 脚手架、六个核心页面、领域模型、Mix/Timer reducer、repository 抽象、Android Media3 播放边界和 Settings 信任文案硬化。现有 OpenSpec change 的 `tasks.md` 均已完成。

但当前 UI 仍主要是静态 MVP：

- `App.kt` 直接用 `remember { mutableStateOf(AppState()) }` 持有全局状态。
- 多个页面控件仍是 no-op：Library 搜索/分类、Mixer 添加/保存/音量、Timer preset/start/extend、Saved Mixes 筛选/播放、Settings toggle、Home 收藏/音量。
- `LocalAppRepository` 与 `AppSnapshot` 已存在，但尚未接入运行时 restore/save。
- `third-party-product-review-2026-06-05.md` 明确指出 app shell 状态所有权和持久化真实性会成为生产 MVP 的主要风险。
- `release-readiness` 文档把 last mix 真实持久化列为未完成项。

## 当前未完成能力

### 产品核心

1. Last mix / saved mixes / settings / timer defaults 未在 app flow 中真实恢复。
2. 保存混音、播放已保存混音、收藏当前混音、调整主音量和 layer 音量缺少 UI 行为。
3. Library 的搜索、分类筛选和添加声音未接入状态。
4. Timer 的 preset、开始、延长、取消和 fade slider 未接入状态。
5. Settings 的 `启动时继续上次混音` toggle 未接入状态。

### 发布准备

1. 真实声音资产仍未替换 `silence_loop.wav`。
2. 公开隐私政策 URL 尚未发布，也未在 Settings 打开。
3. Android 13+ 通知 runtime permission 请求时机和弹窗尚未实现。
4. 真实设备音频 QA 和商店截图素材尚未完成。
5. `docs/compliance-readiness.md` 仍有 Settings placeholder 旧描述，需要在本轮或后续文档清理中同步。

## 方案对比

### 方案 A：在 `App.kt` 中继续直接写 callback

优点：

- 改动最少。
- 可以快速让几个按钮有行为。

缺点：

- 状态、播放副作用、持久化副作用会堆在 composable 中。
- 很难做 TDD；UI 行为只能靠人工点。
- 后续 notification callback、timer tick、restore last mix 会继续扩大复杂度。

结论：不推荐。它会把当前已识别的架构风险继续放大。

### 方案 B：新增小型 `AppStore`

优点：

- 保留现有 reducer，不引入重量级架构。
- 将 app-level intent、状态更新、repository save/restore、playback 调用集中到一个可测试对象。
- UI 只负责渲染 state 和发送 intent，便于逐步替换 no-op callback。
- 能直接补齐 last mix / saved mixes / settings 的 closed testing 前核心缺口。

缺点：

- 需要调整 `App.kt` 和 screen 函数签名。
- 需要为 clock/id 生成注入轻量接口，避免测试不稳定。

结论：推荐。本轮最稳妥，收益最大。

### 方案 C：直接引入完整 ViewModel / use case / DI

优点：

- 后续 Android 生命周期、平台依赖和测试边界更正规。
- 更接近中大型应用结构。

缺点：

- 当前单模块 MVP 会被过早架构化。
- 需要更多依赖和样板，容易拖慢可用 MVP。
- HarmonyOS / CMP 路径尚未稳定，过早绑定 Android ViewModel 不划算。

结论：暂不采用。等 AppStore 复杂度真实上升后再拆 use case 或平台 ViewModel。

## 推荐范围

本 change 采用方案 B：新增小型 commonMain `AppStore`，把当前核心 UI 交互接入 state/reducer/repository/playback。

本 change 不做：

- 真实音频资产采购和替换。
- Android runtime notification permission。
- 公开隐私政策网页托管。
- HarmonyOS 打包 spike。
- 完整 billing、离线下载或账号能力。

## 成功标准

- App 启动时能从 repository restore saved mixes、recent mixes、current mix、timer defaults 和 settings。
- 用户操作核心页面后，state 变化经过统一 intent 路径。
- 用户修改 mix/settings/timer/saved mixes 后，repository 保存最新 snapshot。
- 播放/暂停仍通过 `PlaybackEngine` 边界，不让 common UI 依赖 Android Media3。
- 主要 no-op 控件被替换为真实 callback 或明确禁用/隐藏。
- common unit tests 覆盖 store restore、save side effect、UI intent 行为和 timer/mix 集成。

