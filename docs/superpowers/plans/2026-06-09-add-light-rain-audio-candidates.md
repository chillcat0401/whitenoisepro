# 小雨音频候选执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `add-light-rain-audio-candidates` 执行，以下步骤与 tasks.md 对齐。

**目标：** 在现有 `rain` 候选之外新增内部 `light-rain` profile，用于生成小雨/远雨候选素材。

**范围：** 仅修改内部音频合成工具、测试、OpenSpec 和本地候选输出；不修改 Android 发布资源、App catalog 或发布 manifest。

## 执行清单

- [x] 记录用户反馈：`rain-natural` 2 号可用但偏暴雨
- [x] 创建 OpenSpec explore、proposal、design、spec delta 和 tasks
- [x] 添加 `light-rain` supported profile 红灯测试
- [x] 添加 `light-rain` roughness 低于 `rain` 的差异化测试
- [x] 新增 `light-rain` supported profile
- [x] 实现更轻、更稀疏的 `light-rain` 频谱模型
- [x] 生成 `work/audio-candidates/light-rain-2026-06-09/`
- [x] 确认候选目录被 git ignore
- [x] 同步主规格 `audio-asset-studio`
- [x] 运行目标音频工具测试
- [x] 运行 `node tools/generate_mvp_audio.mjs --verify`
- [x] 运行全部 Node 工具测试
- [x] 运行 OpenSpec strict validation
- [x] 完成代码审查和归档验收记录

## RED / GREEN 记录

- RED：`node --test tools/audio_synthesis_core.test.mjs` 失败，`supportedProfiles.includes("light-rain")` 为 false。
- GREEN：新增 `light-rain` profile 和频谱模型后，目标测试通过。

## 验证记录

- `node --test tools/audio_synthesis_core.test.mjs`
- `node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs`
- `node tools/audio_asset_studio.mjs --profile light-rain --count 5 --seed 260700 --prefix light-rain --duration-seconds 12 --out work/audio-candidates/light-rain-2026-06-09`
- `node tools/generate_mvp_audio.mjs --verify`
- `node --test tools/*.test.mjs`
- `openspec validate add-light-rain-audio-candidates --strict`
