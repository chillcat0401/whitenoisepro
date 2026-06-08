# Tasks

## 1. 依赖与序列化

- [x] 1.1 添加 serialization/DataStore/coroutines-test 依赖
  - Acceptance: serialization plugin 与 Kotlin 版本一致。
  - Acceptance: DataStore 使用稳定版 `1.2.1`。
  - Verification: Gradle dependency resolution 和 compile task 成功。

- [x] 1.2 添加 persistence codec 红灯测试
  - Acceptance: round-trip、未知字段、损坏 JSON、未知 schema version 有测试。
  - Verification: 测试先因 codec 不存在而失败。

- [x] 1.3 实现版本化 persistence DTO 和 codec
  - Acceptance: DTO 与 domain model 分离。
  - Acceptance: codec 通过 1.2 测试。
  - Verification: targeted common test。

## 2. 异步 Repository

- [x] 2.1 添加 Flow/suspend repository 红灯测试
  - Acceptance: 初始 null、save emission、后续 restore emission 有测试。
  - Verification: 测试先因旧同步接口不匹配而失败。

- [x] 2.2 将 AppRepository/AppStorage 改为 Flow + suspend
  - Acceptance: `FakeAppStorage` 使用 MutableStateFlow。
  - Acceptance: commonMain 不依赖 Android。
  - Verification: repository tests 通过。

## 3. AppStore 协程化

- [x] 3.1 将 AppStore 测试迁移到 runTest
  - Acceptance: async restore/save 通过 `advanceUntilIdle` 验证。
  - Acceptance: 初始化状态有测试。
  - Verification: targeted AppStore test。

- [x] 3.2 实现 AppStore CoroutineScope restore/save
  - Acceptance: 不使用 blocking I/O。
  - Acceptance: 首个 snapshot emission 后 `isInitialized=true`。
  - Acceptance: 所有既有行为测试继续通过。
  - Verification: AppStore tests 通过。

## 4. Android DataStore Adapter

- [x] 4.1 添加 AndroidDataStoreAppStorage 测试
  - Acceptance: JSON key 写入和 Flow decode 有测试。
  - Acceptance: 空 preferences 和损坏 JSON 返回 null。
  - Verification: Android unit test。

- [x] 4.2 实现 Preferences DataStore adapter
  - Acceptance: adapter 位于 androidMain storage 包。
  - Acceptance: 使用 DataStore Flow 和 suspend edit。
  - Verification: Android unit test。

- [x] 4.3 MainActivity 注入真实 repository
  - Acceptance: Activity 使用 application-context DataStore 单例。
  - Acceptance: production path 不再使用 FakeAppStorage。
  - Verification: compile + assembleDebug。

## 5. 文档与验证

- [x] 5.1 更新 smoke test 和 release-readiness 文档
  - Acceptance: 文档说明 DataStore 已接入。
  - Acceptance: force-close/relaunch 验证步骤准确。
  - Verification: 文档审查。

- [x] 5.2 全量验证和代码审查
  - Acceptance: `:composeApp:check :composeApp:assembleDebug` 成功。
  - Acceptance: 无 blocker review finding。
  - Verification: 保存 review 记录。
