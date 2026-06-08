# Google Play 产品 MVP 硬化

## 背景

`docs/third-party-product-review-2026-06-05.md` 将当前 WhiteNoisePro 定位为“工程 MVP 候选”，但还不是可用于 Google Play 市场验证的产品 MVP。主要缺口集中在：

- 真实音频资产与授权。
- 隐私政策、Data safety、通知权限说明。
- closed testing 计划和反馈闭环。
- 商店 listing 文案与截图素材。
- Settings 中 placeholder 项的处理策略。
- last mix / saved mixes / settings 等发布前状态恢复体验。

本变更不追求一次性完成全部产品打磨，而是把 closed testing 前必须具备的上架准备和产品硬化项转成可执行任务。

## 用户价值

- 用户能理解应用为什么需要后台播放和通知权限。
- 用户能信任应用的隐私定位：无账号、无广告、无云同步、偏好本地保存。
- closed testing 能收集有效反馈，而不是只完成形式上的测试人数要求。
- 应用用真实声音资产验证核心体验，而不是继续依赖 `silence_loop.wav`。
- 后续进入中国大陆 / Huawei / HarmonyOS 路径时，不会被 Google-only 架构绑定。

## 范围

### 本变更包含

- 建立 Google Play release-readiness checklist。
- 建立 closed testing 计划和反馈模板。
- 建立最小隐私政策与 Data safety 工作表。
- 建立通知权限说明和测试场景。
- 建立 MVP 音频资产验收标准与手动 QA 矩阵。
- 明确 Settings placeholder 项的发布前处理策略。
- 将“shared core 不依赖 Google-only 服务”固化为后续架构规则。

### 本变更暂不包含

- 接入真实第三方 analytics、crash reporting、billing 或广告 SDK。
- 立即采购或生成最终音频资产。
- 立即发布隐私政策网页。
- 立即制作最终商店截图。
- HarmonyOS 打包实现。

## 成功标准

- `docs/release-readiness/` 下存在可执行 checklist 和工作表。
- OpenSpec task 能覆盖 third-party review 中 closed testing 前的主要缺口。
- Google Play 相关判断引用官方文档并标注当前日期。
- 没有新增 Google-only 依赖进入 common source set。
- 后续进入实现阶段时，能按任务逐项执行和验收。

## 风险

- Google Play 政策会变化：所有政策类判断必须保留复核日期和来源。
- 声音资产授权不可假设：没有授权文件前，任何音频只能作为测试资产。
- 过早做 HarmonyOS 会拖慢 Android MVP：HarmonyOS 仍保持 spike 路径，不作为本变更发布 gate。
