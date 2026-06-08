# Android DataStore Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans and superpowers:test-driven-development. Steps use checkbox syntax for tracking.

**Goal:** 使用 AndroidX Preferences DataStore、Flow 和 suspend API 实现真实 Android 进程重启持久化。

**Architecture:** commonMain 使用版本化 DTO/JSON codec 和 Flow/suspend repository；androidMain 使用 Preferences DataStore adapter；AppStore 通过注入 CoroutineScope 异步恢复和保存。

**Tech Stack:** Kotlin 2.2.21, kotlinx.coroutines 1.11.0, AndroidX DataStore Preferences 1.2.1, kotlinx.serialization JSON 1.11.0.

---

## Task 1: Codec

- [ ] 添加依赖。
- [ ] 先写 `AppSnapshotCodecTest` 红灯测试。
- [ ] 实现 `PersistedAppSnapshotV1`、mapper 和 JSON codec。
- [ ] 运行 targeted test。

## Task 2: Async Repository

- [ ] 先把 repository test 改为 `runTest` 和 Flow 验收。
- [ ] 确认旧接口导致测试失败。
- [ ] 实现 Flow/suspend repository 和 MutableStateFlow fake storage。
- [ ] 运行 repository test。

## Task 3: Coroutine AppStore

- [ ] 将 AppStore tests 改为 `runTest`。
- [ ] 添加初始化状态测试。
- [ ] 实现 scope.launch collect/save。
- [ ] 运行 AppStore tests。

## Task 4: Android Adapter

- [ ] 写 fake DataStore 驱动的 Android adapter test。
- [ ] 实现 `AndroidDataStoreAppStorage`。
- [ ] 在 MainActivity 创建 DataStore repository 并注入。
- [ ] 运行 Android unit test 和 compile。

## Task 5: Verification

- [ ] 更新 storage/release docs。
- [ ] 运行 `:composeApp:check :composeApp:assembleDebug`。
- [ ] 代码审查并记录。
