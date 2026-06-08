# 音频焦点播放闸门设计

## 决策

继续使用 Media3 自动 audio focus，但 controller 的 `isPlaying` 成为可听 layer 的唯一
授权信号。engine 的 play 命令只准备资源并进入 Buffering；controller 真正播放后才启动
未静音 layer。

## 状态

- `isPlaying=true`：StartLayers。
- `isPlaying=false && playWhenReady=true`：AwaitAuthorization，layer 全暂停。
- `isPlaying=false && playWhenReady=false`：PauseLayers。

AppStore 删除乐观 `isPlaying=true`，UI 服从 engine StateFlow。

## Android 15

API 35+ 非顶部应用且非前台服务时焦点请求可能失败。该设计不绕过限制；失败时 controller
不会实际播放，layer 闸门保持关闭。

## 验收

- 焦点授权前无可听输出。
- suppression/buffering 期间 engine 非 Playing。
- 明确 pause/stop 后旧 callback 不恢复 layer。
- 自动测试、构建、签名、资产与 OpenSpec 验证全部通过。
