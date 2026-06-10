# 代码评审:fix-media-notification-foreground(2026-06-10)

范围:单提交,addSession 一行 + MainActivity 权限请求 + Manifest + 文档。

- 行为缺陷:无。addSession 在 onCreate 创建后立即注册,onDestroy 既有
  release 流程不变;权限请求带 SDK 门(API<33 直接返回),拒绝路径降级明确。
- 测试:平台层(通知/前台/冻结)不可单测,以模拟器实证替代并留档;
  权限请求函数逻辑简单且有 SDK 门,不强行抽象。
- 风险:首启多一个权限弹窗,文案与拒绝降级已在权限决定文档明确;
  华为 Android 10 无弹窗(API<33),通知应直接出现——待真机回归确认。

结论:通过。
