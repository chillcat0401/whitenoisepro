## 1. 上架巡查与文档一致性

- [x] 1.1 更新 `google-play-checklist.md`，同步 8 个发布声音、targetSdk 36、签名 AAB、closed testing、隐私 URL 和真实设备 QA 状态
- [x] 1.2 新增 Google Play 上架巡查报告，按 `ready` / `blocked` / `needs-human-evidence` 标注当前问题和负责人输入项
- [x] 1.3 编写隐私政策模板和开发者信息模板；未知主体、邮箱、地址、URL、生效日期使用明确占位
- [x] 1.4 更新 Data safety、privacy policy draft、audio asset QA 和 closed testing 文档，移除四声音旧状态并补齐 8 声音证据

## 2. 红灯测试

- [x] 2.1 添加 SettingsContent 测试，锁定无账号、无广告、本地偏好、第一方生成声音、后台媒体控制和不宣称医疗效果
- [x] 2.2 添加 MixReducer / AppStore 测试，锁定重复保存同一混音不会生成重复 Saved row
- [x] 2.3 添加 Home / Timer 文案或状态模型测试，锁定推荐 30 或 45 分钟入睡 timer 路径

## 3. 基础功能增强实现

- [x] 3.1 更新 Settings release-ready 内容，使其与隐私、权限、第一方声音和上架状态一致
- [x] 3.2 实现保存混音去重逻辑，保持修改后的混音仍可保存为独立条目
- [x] 3.3 在 Home / Timer 中增加推荐入睡 timer 入口，并确保启动后 Mini Player 显示剩余时间
- [x] 3.4 轻量优化 Library 分类和推荐声音入口，不改变现有播放架构

## 4. 验证与归档准备

- [x] 4.1 运行目标单元测试
- [x] 4.2 运行 `testDebugUnitTest`
- [x] 4.3 运行 `lintDebug` 和 `assembleDebug`
- [x] 4.4 复核 OpenSpec strict validation，并更新任务状态
