# 五天 Google Play Closed Testing Readiness Summary

日期：2026-06-09

## 结论

本地工程侧 release candidate 已达到 closed testing 上传前的技术 ready 状态：19 个声音目录已入包，signed AAB 已构建并通过 verifier，Node 工具测试、Gradle 单元测试、lint、release APK assemble 和模拟器 release smoke 均通过。

当前不能直接声明 closed testing submission ready，因为仍缺少开发者账号侧和人工运营侧输入：公开隐私政策 URL、开发者信息、支持邮箱、Play App Signing 证据、upload key 备份、tester 名单、反馈渠道和真实设备音频 QA。

## 已完成

- 11 个 Freesound CC0 种子素材已处理为 Android release raw resources，并记录在 `docs/audio-assets/external-release-audio-manifest.json`。
- App catalog 现在包含 19 个声音，Android resolver 对每个发布 sound id 有 dedicated raw resource。
- GitHub Pages-ready 隐私政策页面已新增：`site/privacy-policy.html`。
- Store listing、Data safety、Play Console worksheet、developer info template 和 closed testing checklist 已更新到当前 19 声音版本。
- 轻量 UI polish 已完成：更暖的深色调、松弛 spacing、底部 chrome 高度同步、外部声音 icon 覆盖。
- release candidate 证据已记录在 `docs/release-readiness/release-candidate-2026-06-09.md`。

## 本地验证

| 检查项 | 状态 |
| --- | --- |
| `node --test tools/*.test.mjs` | pass，24 tests |
| `:composeApp:testDebugUnitTest` | pass |
| `:composeApp:lintDebug` | pass |
| `:composeApp:bundleRelease` | pass |
| `tools/verify_release_bundle.mjs` | pass |
| `:composeApp:assembleRelease` | pass |
| `emulator-5554` release smoke | pass |
| OpenSpec strict validation | pass，`openspec validate --all --strict` |

## 外部阻断项

| 阻断项 | Owner | 当前状态 |
| --- | --- | --- |
| GitHub Pages 隐私政策公开 URL | 开发者 | 页面已准备，需部署并确认 HTTPS URL |
| 开发者展示名 / 主体 | 开发者 | blocked placeholder |
| 支持邮箱 | 开发者 | blocked placeholder |
| 隐私联系邮箱 | 开发者 | blocked placeholder |
| Play App Signing 状态和 upload certificate 核对 | 开发者 | 需要 Play Console 账号侧截图或确认 |
| upload key 离线备份 | 开发者 | 需要备份位置和密码管理确认 |
| 12+ tester roster / Google Group / email list | 开发者 | 模板已准备，名单未补齐 |
| 真实 Android 设备音频 QA | 开发者 + QA | 未完成，模拟器不能替代听感和蓝牙/锁屏/打断测试 |
| 反馈渠道 | 开发者 | 需要表单、群或邮箱 |

## 五天执行建议

1. 第 1 天：部署 GitHub Pages 隐私政策；补开发者信息和支持邮箱；在 Play Console 完成 app setup 和 Data safety 表单。
2. 第 2 天：上传 signed AAB 到 internal testing 或 closed testing；核对 Play App Signing / upload certificate；准备 tester roster。
3. 第 3 天：邀请 12-20 名 tester，确认 opt-in；发布测试说明和反馈渠道；开始记录问题。
4. 第 4 天：完成至少 2 台真实 Android 设备音频 QA，重点听感、loop、后台、锁屏、蓝牙和打断恢复。
5. 第 5 天：修复阻断反馈；更新 store screenshots；冻结 release candidate；进入 closed testing 14 天运营窗口。

## 官方规则核对

- Google Play 对 2023-11-13 后创建的新个人开发者账号要求 closed testing 至少 12 名 tester 连续 opt-in 14 天，之后才能申请 production access。来源：`https://support.google.com/googleplay/android-developer/answer/14151465?hl=en`
- Google Play 要求在 App content 中提交在线托管的 privacy policy URL；隐私政策应在 app store listing 和 app 内可用，且覆盖用户隐私。来源：`https://support.google.com/googleplay/android-developer/answer/9859455?hl=en-EN`

## 下一步

优先执行账号侧事项，而不是继续扩功能。技术侧下一项高价值工作是生成正式商店截图和 feature graphic，但它应在隐私政策 URL、开发者信息和 tester roster 至少完成后进行。
