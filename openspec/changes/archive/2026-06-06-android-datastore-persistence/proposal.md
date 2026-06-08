# Android DataStore 协程持久化

## 为什么

当前应用的 AppStore 已有状态和 repository 边界，但 Android 默认 repository 使用内存 Fake Storage。进程结束后用户数据会丢失，无法满足 last mix、saved mixes、timer defaults 和 settings 的 MVP 验收。

本变更采用 AndroidX Preferences DataStore 和 Kotlin 协程，将 repository 升级为 Flow/suspend 异步边界。

## 用户故事

- 用户保存混音、调整设置或 timer 后，强制关闭并重新打开应用，数据仍然存在。
- 用户关闭 `启动时继续上次混音` 后，重启应用仍保留 saved mixes/settings，但不恢复上次 current mix。
- 存储内容损坏时，应用使用默认状态启动，而不是崩溃。

## 范围

- 引入 AndroidX DataStore Preferences 稳定版。
- 引入 Kotlin serialization plugin 和 JSON runtime。
- 新增版本化 persistence DTO 和 codec。
- 将 `AppRepository` / `AppStorage` 改为 Flow + suspend。
- 将 `AppStore` 改为协程驱动 restore/save。
- Android `MainActivity` 注入 DataStore repository。
- 添加 common codec/repository/store 测试和 Android storage 测试。
- 更新 storage smoke test 和 release-readiness 状态。

## 非目标

- Proto DataStore。
- 数据库。
- 云同步和账号。
- 存储加密。
- HarmonyOS 持久化实现。
- notification permission、隐私 URL、真实音频资产。

## 验收

- DataStore 使用 `Flow<Preferences>` 读取和 suspend `edit` 写入。
- snapshot JSON 包含 schema version。
- JSON decode 忽略未知字段。
- 空 key、空内容或损坏 JSON 返回 null，并允许 AppStore 使用默认状态。
- AppStore 不阻塞主线程读取 repository。
- AppStore 的持久化写入通过注入 CoroutineScope 执行。
- Activity 不再注入 Fake storage。
- 进程重启 smoke test 文档可直接执行。

## 依赖版本

- `androidx.datastore:datastore-preferences:1.2.1`
- Kotlin serialization compiler plugin 与 Kotlin `2.2.21` 对齐。
- `kotlinx-serialization-json:1.11.0`。
- `kotlinx-coroutines-core:1.11.0`。
