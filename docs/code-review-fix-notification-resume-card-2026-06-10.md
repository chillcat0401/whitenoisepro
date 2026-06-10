# 代码评审:fix-notification-resume-card(2026-06-10)

- 行为缺陷:无。门控新状态只在 PauseLayers 落地后置位、cancelPlay/重新授权时
  清除,stale-reject 原语义由既有测试钉死;ForwardingPlayer 仅作用于会话侧,
  引擎声层播放器不受影响。
- 测试:门控 5 用例;卡片样式为平台渲染,以截图证据替代。
- 风险:蓝牙耳机按键走同一 MediaSession 路径,理论上同样修复,无外设未实测
  (与 2.4 记录一致)。

结论:通过。
