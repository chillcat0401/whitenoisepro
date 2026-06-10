# Release Candidate 2026-06-10(v0.2.0,closed testing 首包候选)

## 构建

- versionCode 1 / versionName 0.2.0(targetSdk 36 / minSdk 26)
- AAB:`composeApp/build/outputs/bundle/release/composeApp-release.aab`
  - 24.1MB,SHA-256 `a40d8a9b7aa5f1a9c183cff780e9cf746515b95a9fddbe38c1cdac9390345f0b`
  - `jarsigner -verify` 通过;upload key CN=WhiteNoisePro Upload,证书有效期至 2053,
    SHA-256 指纹 `6278bb97…c06385`
- `:composeApp:check`(含归档验收门禁)全绿

## 本 RC 相对 0.1.0 的内容

- 夜间暖调 UI 重构(层级焦点 + 播放呼吸光晕 + 环形定时器)
- 声音目录 8 → 21:18 个 CC0 真实素材(全许可证快照取证)+ 3 个运行时合成噪声;
  新增「环境」分类与 6 个新品类图标
- 一方合成 WAV 移除,旧 soundId 解码迁移;AAB 较改造前缩小约 7MB
- 隐私政策已发布:https://chillcat0401.github.io/whitenoisepro/privacy-policy.html(HTTP 200)

## 验证记录

- 模拟器(Pixel 9 Pro / API 36):release APK 安装、启动、五页导航、
  合成 + 真实素材混合播放、状态恢复全部正常
- 真机(华为 LYA-AL00 / Android 10):release APK 安装成功、Activity 正常 resume、
  无崩溃日志;**交互冒烟与听测被锁屏阻塞,待开发者解锁后完成**
- 7 个新素材机器接缝 QA 全 PASS(见 audio-asset-qa.md 2026-06-10 节)
- 商店截图:docs/store-assets/screenshots/phone/ 五张 1080×2160(2:1,符合 Play 规格),
  基于新 UI 与播放态拍摄

## 上传前仍需人工完成(按依赖顺序)

1. Google Play 开发者账号通过审核(进行中,外部等待)
2. 隐私政策页与 listing 的开发者主体、联系邮箱、生效日期占位补齐(信息在用户手里)
3. 真机解锁后:音频主观听测(7 个新素材 + 整体回归),manifest releaseDecision 改 human-pass
4. 测试者名单 ≥14 人确认(12 人 ×14 天门槛留余量)
5. Play Console 建应用 → 上传本 AAB 至 closed testing → Data safety 按工作表填报
