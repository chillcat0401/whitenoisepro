# Code Review: Android DataStore 协程持久化

日期：2026-06-06

## 范围

- AndroidX Preferences DataStore 1.2.1。
- kotlinx.coroutines 1.11.0。
- kotlinx.serialization JSON 1.11.0。
- Flow/suspend repository。
- AppStore 异步 restore/save。
- Android DataStore adapter 和 MainActivity 注入。

## 结论

未发现 blocker。

## 关键检查结果

- production Activity 路径已注入 `AndroidDataStoreAppStorage`，不再使用内存 Fake storage。
- DataStore instance 通过 application context 顶层 delegate 创建，避免同一文件多实例。
- common repository 不依赖 Android，并使用 `Flow<AppSnapshot?>` 与 suspend `save`。
- AppStore 不进行 blocking I/O；首个 snapshot emission 完成后才渲染主 UI。
- JSON schema 有显式 version，未知字段可忽略，损坏 JSON 和不支持版本回退到默认状态。
- DataStore adapter 读取 IOException 时回退为空 preferences。

## 剩余风险

- 尚未在真实设备执行 force-stop/relaunch smoke test；步骤已写入 `docs/android-storage-smoke-test.md`。
- persistence 写入错误当前不会显示给用户；DataStore 写入异常会停留在协程异常处理层，后续可增加非阻塞错误状态。
- 当前使用单 JSON snapshot，适合 MVP 小数据量；若 saved mixes 数量和资源元数据显著扩大，应重新评估 Proto DataStore 或数据库。
- 数据未加密。当前内容仅为非敏感偏好和混音配置；未来若存储账号、购买或敏感数据，需要单独安全设计。

## 验证

以下命令通过：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug
```

结果：`BUILD SUCCESSFUL`。

