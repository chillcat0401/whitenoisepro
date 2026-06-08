# 睡眠定时器运行时闭环

## 范围

- AppStore 启动 timer coroutine job。
- 每秒 Tick timer state。
- 每 tick 调用 TimerPlaybackCoordinator 应用 fade/stop。
- StartTimer 时若未播放，启动当前 mix。
- CancelTimer 恢复 fade factor 1。
- 修复 active timer Extend。
- timer 完成时保存最终状态。
- 添加协程虚拟时间测试。

## 非目标

- 进程被系统杀死后仍独立运行的 Alarm/Service timer。
- 自定义 fade 时长 UI。
- KeepPlaying UI 选择。

## 验收

- 虚拟时间推进后 remainingMillis 正确减少。
- 进入 fade window 后 engine fade factor 正确。
- 到点 engine.stop，AppState.isPlaying=false。
- Cancel 停止 ticker 并恢复 fade factor。
- Extend 后下一 tick 保留完整延长时间。

