# Design: 睡眠定时器运行时闭环

AppStore 持有一个 `Job?`：

- StartTimer：reducer Start、确保播放、应用 coordinator、启动 loop。
- loop：`delay(1000)`，用 AppClock 生成 Tick，更新 state，应用 coordinator。
- completion：设置 `isPlaying=false`，保存 snapshot，结束 job。
- CancelTimer：取消 job、reducer Cancel、fade factor 恢复 1、保存。
- restore：若 snapshot timer 仍 active，立即 Tick；若仍未完成则重启 ticker，但不自动恢复音频播放。

Tick 不每秒写 DataStore，只在 start/extend/cancel/completion 保存，避免持续写盘。

