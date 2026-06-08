# Tasks

- [x] 1.1 更新 Settings 内容红灯测试
  - Acceptance: 不存在“通知权限” row。
  - Acceptance: 存在“后台媒体控制”说明。
  - Verification: targeted test 先失败。

- [x] 1.2 更新 Settings 内容实现
  - Acceptance: 新文案通过测试。
  - Verification: targeted test。

- [x] 2.1 删除 Manifest POST_NOTIFICATIONS
  - Acceptance: Manifest 只保留后台媒体播放所需权限。
  - Verification: `rg` 静态检查和 assembleDebug。

- [x] 2.2 更新当前权限和 release 文档
  - Acceptance: 当前状态文档不再要求通知 runtime permission。
  - Acceptance: 记录 MediaSession exemption 官方依据。
  - Verification: `rg` 检查。

- [x] 3.1 全量验证和代码审查
  - Acceptance: check + assembleDebug 成功。
  - Verification: review note。
