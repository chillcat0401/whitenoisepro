# Explore: 音频焦点播放闸门

## 问题

当前 MediaSession controller player 负责 Media3 audio focus，但
`AndroidPlaybackEngine.play()` 会先直接启动所有 layer players，随后才要求 controller
播放。这意味着 controller 的焦点请求尚未成功、被延迟或被 Android 15 拒绝时，可听
layer 已经开始输出。

Media3 文档说明 `Player.isPlaying` 只有在 player 已就绪、`playWhenReady=true` 且没有
playback suppression 时才为 true。Android 官方文档同时说明：

- Android 12+ 会在焦点切换时执行系统淡出或静音。
- Android 15 / API 35+ 中，非顶部应用且未运行前台服务时，audio focus 请求会失败。

## 目标

- 没有 controller 实际播放授权时，任何 layer 都不得输出。
- 焦点延迟、suppression 和初始 buffering 期间保持安全静音。
- 瞬时焦点恢复后，由 controller 的真实 `isPlaying` 恢复 layer。
- 显式 pause/stop 不被异步 controller callback 重新启动。
- 保持 Media3 自动 audio focus，不引入第二套 `AudioManager` 状态机。

## 方案比较

### A. 维持现状，只增加真机检查

优点：无代码改动。  
缺点：已知存在焦点授权前出声，Android 15 请求失败时行为不安全。

### B. Controller 实际播放作为 layer 闸门

engine 的 play 只准备 layer 并进入 Buffering；controller `isPlaying=true` 时才启动 layer。
`playWhenReady=true` 但尚未播放时保持 Buffering；`playWhenReady=false` 时暂停 layer。

优点：复用 Media3 焦点策略，状态来源单一，改动集中且可测试。  
缺点：播放按钮不再能假定命令发出即成功，需要 UI 服从 engine 状态。

### C. 自建 AudioManager focus coordinator

engine 直接请求 `AudioFocusRequest`，controller 不管理焦点。

优点：可完全自定义 delayed focus 和 ducking。  
缺点：与 Media3/MediaSession 状态重复，容易产生双重焦点和恢复竞态。

## 决策

采用方案 B。controller player 继续是唯一 audio focus 所有者；layer player 只在
controller `isPlaying=true` 后播放。

## 非目标

- 不实现自定义 duck 音量曲线。
- 不替换 MediaSession controller。
- 不新增音频权限或通知权限。
- 不处理 HarmonyOS 音频焦点。
- 不承诺厂商设备上完全一致的瞬时焦点恢复策略。
