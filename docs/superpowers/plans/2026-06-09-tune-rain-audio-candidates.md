# 雨声音频候选调音执行计划

> 计划用于归档验收证据。该变更已按 OpenSpec `tune-rain-audio-candidates` 执行，以下步骤与 tasks.md 对齐。

**目标：** 基于用户听感反馈，降低 rain 候选的“窄口花洒”质感，生成一组更柔和、宽频、自然的 rain 候选用于人工听测。

**范围：** 仅调整内部程序化 rain 合成模型、测试和本地候选输出；不修改 Android 发布资源、声音目录或发布 manifest。

## 执行清单

- [x] 记录用户反馈和探索结论
- [x] 创建 OpenSpec proposal、design、spec delta 和 tasks
- [x] 添加 rain texture 红灯测试，锁定过强相邻采样跳变
- [x] 确认旧 rain 模型在新门槛下失败
- [x] 调整 rain 频谱模型，降低窄高频喷流感并增加宽频雨幕能量
- [x] 保持 deterministic WAV、RMS、peak、loop boundary 和非法 profile 测试通过
- [x] 生成 `work/audio-candidates/rain-natural-2026-06-09/`
- [x] 确认新候选 manifest 与 audition HTML 存在
- [x] 确认候选目录被 git ignore
- [x] 运行目标 Node 测试
- [x] 运行 `node tools/generate_mvp_audio.mjs --verify`
- [x] 运行全部 Node 工具测试
- [x] 运行 OpenSpec strict validation
- [x] 同步主规格并完成代码审查

## RED / GREEN 记录

- RED：`node --test tools/audio_synthesis_core.test.mjs` 中 `rain profile limits harsh adjacent-sample roughness` 失败，旧 rain `p99AdjacentDelta=0.1987060151982177`。
- GREEN：调音后目标测试通过，新候选 `p99AdjacentDelta` 约为 `0.088-0.089`。

## 验证记录

- `node --test tools/audio_synthesis_core.test.mjs tools/audio_asset_studio.test.mjs`
- `node tools/generate_mvp_audio.mjs --verify`
- `node tools/audio_asset_studio.mjs --profile rain --count 4 --seed 260620 --prefix rain-natural --duration-seconds 12 --out work/audio-candidates/rain-natural-2026-06-09`
- `openspec validate tune-rain-audio-candidates --strict`
- `node --test tools/*.test.mjs`
