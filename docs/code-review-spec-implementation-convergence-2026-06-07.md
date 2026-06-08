# Code Review: 规格与实现回归

日期：2026-06-07

## Findings

审查期间发现并修复：

- P0：MediaSession controller ExoPlayer 会播放棕噪声，可能叠加到真实多层混音；现固定静音。
- P1：保存混音重命名/收藏最初用 saved 副本覆盖 current/recent，可能丢失尚未保存的实时音量编辑；现只同步标题或收藏字段。
- P1：Timer 缺少 120 分钟与自定义时长入口。
- P1：Mixer 缺少 mute/unmute/remove。
- P1：Saved Mixes 缺少 rename/favorite。
- P1：Mini Player 未显示活动 timer。
- P2：Sound metadata 缺少 loop asset key 和 default volume。
- Spec：Restore Purchases、通知权限和自然声音示例与后续决策冲突。

当前未发现阻塞本次归档的代码问题。

## Tests

- 新增 SoundCatalog asset/default volume 测试。
- 新增 MixReducer rename/favorite 和 current edit 保留测试。
- 新增 AppStore add/remove/mute/rename/favorite/custom timer 测试。
- 新增 Mini Player subtitle 和 120 分钟预设测试。
- 新增 Android MediaSession controller volume policy 测试。

## Spec Alignment

- `mix-management` 的 remove、mute、saved edit/favorite 已接入 UI 和 AppStore。
- `sleep-timer` 的全部预设、自定义时长和 Mini Player 剩余时间已落实。
- `sound-catalog` metadata 已包含 asset key 与 default volume。
- `playback-engine` 不再由 controller player 添加额外可听层。
- `settings-compliance` 与 `settings-trust` 对 billing 未集成时的行为一致。
- `release-readiness` 与 Manifest 的无 `POST_NOTIFICATIONS` 策略一致。

## Remaining Risks

- 当前机器没有可用 AVD 或连接设备，未完成本轮新增控件的截图和点击验证。
- 必须在真实设备验证 Mixer 两个 44dp 操作按钮在 360px 宽度的可用性。
- 系统媒体控制改变播放状态后，前台 AppStore UI 不会主动观察 engine 状态；重新进入前台时图标可能短暂不同步，建议后续单独 change 处理。
- MediaSession controller player 虽静音，仍是独立 ExoPlayer；后续可评估 forwarding Player，当前方案以最小风险保障不产生额外声音。
