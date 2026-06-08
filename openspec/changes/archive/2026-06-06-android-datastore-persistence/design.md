# Design: Android DataStore 协程持久化

## 数据格式

Preferences DataStore 中使用一个 string key：

```text
app_snapshot_json
```

JSON 顶层为 `PersistedAppSnapshotV1`：

- `schemaVersion = 1`
- saved mixes
- recent mixes
- current mix
- timer defaults
- settings

持久化 DTO 与 domain model 分离，避免存储 schema 直接绑死运行时领域模型。

JSON 配置：

- `encodeDefaults = true`
- `ignoreUnknownKeys = true`
- `explicitNulls = false`

## 异步 repository

```kotlin
interface AppRepository {
    val snapshots: Flow<AppSnapshot?>
    suspend fun save(snapshot: AppSnapshot)
}

interface AppStorage {
    val snapshots: Flow<AppSnapshot?>
    suspend fun write(snapshot: AppSnapshot)
}
```

`LocalAppRepository` 只委托 storage。

`FakeAppStorage` 使用 `MutableStateFlow`，继续支持 common unit test。

## Android adapter

`AndroidDataStoreAppStorage` 位于：

```text
composeApp/src/androidMain/kotlin/com/whitenoisepro/storage/
```

职责：

- 从 `DataStore<Preferences>.data` 读取 JSON key。
- 调用 common codec decode。
- 对 DataStore I/O 异常返回 null。
- 使用 `dataStore.edit` 原子写入编码后的 JSON。

DataStore instance 使用 application context 单例 delegate，避免同一文件创建多个实例。

## AppStore 生命周期

AppStore 注入 `CoroutineScope`，默认不自己创建不可控的全局 scope。

- 初始化：scope.launch collect repository snapshots。
- 首次 emission：
  - null -> 使用默认 AppState。
  - snapshot -> 按 startLastMix 规则恢复。
  - 设置 `isInitialized = true`。
- 保存：state 先同步更新，然后 scope.launch 调用 repository.save。

测试使用 `runTest` 和 `TestScope`，通过 `advanceUntilIdle` 验证 restore/save。

## UI 初始化

`WhiteNoiseProApp` 使用 `rememberCoroutineScope()` 创建 AppStore。

在 store 尚未收到首个 repository emission 时显示当前主题下的轻量空白/加载状态，避免默认 sample content 短暂闪现后再被真实数据覆盖。

## 错误策略

- JSON 损坏：codec 返回 null。
- DataStore 读取 IOException：发出 null。
- 写入异常：本轮不向 UI 暴露错误，但协程不会阻塞 UI；后续可增加 persistence error state。
- 未知 schemaVersion：返回 null，避免错误解释未来格式。

## 测试

- codec round-trip。
- codec 忽略未知字段。
- codec 损坏/未知版本返回 null。
- repository Flow 和 suspend save。
- AppStore async restore/save。
- Android DataStore adapter 使用 fake `DataStore<Preferences>` 测试 key 读写。
- 全量 Gradle check 和 assembleDebug。

