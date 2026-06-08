## Why

上一轮素材、icon 和 UI 节奏已经归档，项目可以进入更现实的 Google Play 上架巡查。当前主要风险不在 targetSdk 或签名基础设施，而在发布资料一致性、真实设备 QA 证据、隐私 URL、closed testing 组织，以及首次使用路径还缺少足够明确的睡前引导。

## What Changes

- 更新 Google Play release readiness 文档，使其与 8 个第一方生成声音、当前 Manifest、targetSdk 36、签名 AAB 和 closed testing 要求一致。
- 增加一份可执行的上架巡查报告，标注 blocked / ready / needs-human-evidence 状态。
- 编写隐私政策与开发者信息模板；无法确认的主体、邮箱、地址、URL、生效日期等字段先使用明确占位，不伪造信息。
- 强化 Settings / release copy 中的隐私政策 URL 状态、开发者信息占位、第一方声音来源说明和后台媒体控制说明。
- 增强基础体验，但保持小范围：在首屏和 Timer 中更明确地呈现推荐入睡 timer，在保存混音时避免重复保存同一个标题和层组合，在 Library 中让用户更容易从类别进入推荐声音。
- 不引入 analytics、crash SDK、billing、广告、账号、云同步、联网素材或 Google Play Services。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `release-readiness`: 增加 Google Play 上架巡查、closed testing、Data safety / privacy、隐私政策与开发者信息模板、真实设备 QA 和素材证据的一致性要求。
- `settings-trust`: Settings 必须反映当前发布状态，包括隐私政策 URL 未发布时的安全文案、开发者信息占位、第一方本地声音和后台媒体控制说明。
- `sleep-timer`: 首次使用和 Home / Timer 入口必须清晰呈现推荐入睡 timer，并能从主路径启动。
- `mix-management`: 保存混音应避免用相同标题和相同层组合产生重复条目，降低 closed testing 中的困惑。

## Impact

- 影响文档：`docs/release-readiness/*`、新增上架巡查报告、隐私政策模板、开发者信息模板、必要时更新 Data safety worksheet。
- 影响代码：`SettingsContent`、Home / Timer / Library / Saved 相关 UI、`MixReducer` 或保存逻辑、对应 common tests。
- 影响验证：需要重新运行 unit tests、lint、debug assemble；release AAB 和真实设备 QA 作为上架 gate 证据记录。
