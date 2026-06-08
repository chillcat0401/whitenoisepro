# Superpowers 归档验收审计

日期：2026-06-07

## 2026-06-08 追溯验收更新

原审计发现的 12 个流程证据缺口已建立机器可读的 retrospective 记录；原本完整的 2 个
归档已建立 strict 记录。补充记录没有回填旧 plan，也没有把当前测试结果表述为历史 RED。

当前状态：

- `docs/superpowers/acceptance/` 覆盖全部 14 个历史归档。
- 12 个为 `retrospective`，显式保留未勾选 plan、缺少 plan/review/explore 或无法重建 RED
  的事实。
- 2 个为 `strict`：`audio-focus-playback-gate` 和
  `media-control-state-audio-focus`。
- `node tools/verify_archive_acceptance.mjs --all-archives` 全部通过。
- Gradle `check` 已接入历史 archive gate。
- 新变更必须在归档前通过单 change strict gate。

## 审计口径

本报告区分两类结论：

- 当前主线验收：所有归档合并后的代码、规格和发布产物在当前状态下是否通过。
- 历史流程验收：每个归档是否留下完整、可追溯的 Superpowers TDD、执行计划、验证和代码审查证据。

严格流程通过要求：

1. OpenSpec 任务全部勾选并已归档。
2. 适用任务有先 RED、后 GREEN 的 TDD 记录。
3. 执行计划存在时，计划清单已同步完成。
4. 有对应验证结果和代码审查记录。
5. 当前合并结果仍通过完整回归。

缺失的历史记录不能用当前测试结果倒推，也不能通过事后勾选伪造。

## 总体结论

不是所有历史归档都能证明曾严格通过 Superpowers 流程验收；这些缺口现已用
retrospective 记录准确固化，并纳入自动 gate。

- 14 个 OpenSpec 归档的 `tasks.md` 均已全部勾选。
- 当前主线完整回归通过，说明累计实现处于可构建、可测试、可产出签名 AAB 的状态。
- 只有 2 个归档具有完整且同步勾选的 Superpowers 执行计划：
  - `audio-focus-playback-gate`
  - `media-control-state-audio-focus`
- 5 个归档存在 Superpowers 执行计划，但计划仍全部未勾选。
- 7 个归档没有独立 Superpowers 执行计划。
- 3 个归档没有 `explore.md`。
- `bootstrap-cmp-mvp-architecture` 和 `google-play-product-hardening` 没有可明确一一对应的专项代码审查文件。

## 逐项结论

| OpenSpec 归档 | 结论 | 主要依据或缺口 |
| --- | --- | --- |
| `android-background-timer-runtime` | 流程证据不完整 | 有 TDD、验证和专项评审；Superpowers plan 20 项未勾选 |
| `android-datastore-persistence` | 流程证据不完整 | 有红灯任务、构建验证和专项评审；Superpowers plan 19 项未勾选 |
| `app-state-interaction-persistence` | 流程证据不完整 | 有 TDD、验证和专项评审；Superpowers plan 21 项未勾选 |
| `bootstrap-cmp-mvp-architecture` | 证据不足 | 无 explore、无 Superpowers plan、无明确专项评审 |
| `google-play-product-hardening` | 证据不足 | 无 explore、无 Superpowers plan、无明确专项评审；主要为产品和文档硬化 |
| `media-notification-permission-minimization` | 流程证据不完整 | 有红灯任务、验证和专项评审；无 Superpowers plan |
| `settings-trust-hardening` | 流程证据不完整 | 有 RED/GREEN 要求、测试和专项评审；无 Superpowers plan |
| `sleep-timer-runtime` | 流程证据不完整 | 有红灯任务、完整验证和专项评审；无 Superpowers plan |
| `audio-focus-playback-gate` | 严格通过 | OpenSpec、已勾选 plan、TDD、全量验证和专项评审完整 |
| `launcher-branding-baseline` | 流程证据不完整 | 有测试、验证和专项评审；无 explore、无 Superpowers plan、缺少明确 RED 记录 |
| `media-control-state-audio-focus` | 严格通过 | OpenSpec、已勾选 plan、TDD、全量验证和专项评审完整 |
| `publishable-audio-baseline` | 流程证据不完整 | 有红灯任务、资产验证和专项评审；Superpowers plan 18 项未勾选 |
| `signed-release-bundle` | 流程证据不完整 | 有测试先行、签名验证和专项评审；Superpowers plan 19 项未勾选 |
| `spec-implementation-convergence` | 流程证据不完整 | 有多项红灯任务、全量验证和专项评审；无 Superpowers plan |

## 本轮当前主线验证

执行：

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home \
DO_NOT_TRACK=1 \
./gradlew clean check lintDebug assembleDebug bundleRelease
```

结果：

- `BUILD SUCCESSFUL`
- Kotlin/Android 测试 170 个，0 failures，0 errors，0 skipped
- Android lint 通过
- Debug APK 构建通过
- Release AAB 构建和签名通过

补充验证：

- `node --test tools/*.test.mjs`：3 passed，0 failed
- `node tools/generate_mvp_audio.mjs --verify`：4 个音频资产通过
- `node tools/verify_release_bundle.mjs ...`：签名 AAB 通过
- `openspec validate --specs --strict --no-interactive`：13 passed，0 failed

## 处理建议

1. 不要直接把旧 Superpowers plan 全部改为已完成；现有证据不足以证明每一个历史步骤真实执行。
2. 为 12 个非严格通过归档建立一份“追溯验收”变更，只补验证证据和文档，不重写已经工作的生产代码。
3. 对 `bootstrap-cmp-mvp-architecture` 和 `google-play-product-hardening` 优先补专项对齐审查。
4. 对 5 份未勾选 plan，逐项映射现有测试、实现和评审；能证明的标记为“追溯确认”，不能证明的重新执行验证。
5. 后续归档前增加自动 gate：OpenSpec tasks 无未勾选项、Superpowers plan 无未勾选项、专项 review 存在、完整验证命令成功。
