# Explore: Android DataStore 协程持久化

日期：2026-06-06

## 当前问题

`AppStore` 已能通过 `AppRepository` restore/save，但默认运行路径仍使用 `FakeAppStorage`。应用进程结束后，saved mixes、current mix、timer defaults 和 settings 会丢失。

现有 `AppRepository` / `AppStorage` 是同步接口，不适合 AndroidX DataStore：

- DataStore 通过 `Flow<T>` 异步读取。
- DataStore 通过 suspend `edit` / `updateData` 原子写入。
- 在 Activity 或 AppStore 构造阶段阻塞读取会违反协程和主线程安全要求。

## 方案比较

### 方案 A：SharedPreferences

优点：依赖少，同步接口容易接入。

缺点：不是用户指定的 DataStore；同步 API 容易引入主线程 I/O；并发更新和错误处理较弱。

结论：不采用。

### 方案 B：Preferences DataStore + 版本化 JSON snapshot

优点：

- 使用稳定 AndroidX DataStore。
- 读取是 Flow，写入是 suspend 原子事务。
- 单一 JSON snapshot 与当前 `AppSnapshot` 聚合边界匹配。
- 版本化 DTO 可处理字段新增和迁移。
- 不需要现在引入数据库或 protobuf schema 工具链。

缺点：

- 需要 serialization plugin 和 JSON runtime。
- snapshot 每次整体写入，但当前数据量很小。

结论：推荐。

### 方案 C：Proto DataStore

优点：强类型 schema、迁移边界清晰。

缺点：需要 protobuf schema、代码生成和额外构建配置；当前 MVP 数据量和复杂度不足以抵消成本。

结论：未来数据模型显著扩大时再考虑。

## 推荐设计

- Android 使用 Preferences DataStore `1.2.1`。
- DataStore 只保存一个 `app_snapshot_json` key。
- commonMain 定义版本化 `PersistedAppSnapshotV1` 和 JSON codec。
- `AppRepository` 改为：

```kotlin
val snapshots: Flow<AppSnapshot?>
suspend fun save(snapshot: AppSnapshot)
```

- `AppStore` 注入 `CoroutineScope`：
  - 初始化时 collect repository snapshots。
  - 每次持久化操作通过 scope.launch 调用 suspend save。
  - 暴露 `isInitialized`，首个 snapshot emission 后置为 true。
- Android `MainActivity` 创建 DataStore-backed repository 并注入 `WhiteNoiseProApp`。

## 非目标

- 云同步。
- 数据加密。
- 多进程 DataStore。
- Proto DataStore。
- 旧 SharedPreferences 迁移。
- HarmonyOS DataStore 实现。

