## Why

当前 WhiteNoisePro 的文案整体准确，但偏功能说明，缺少一句能被用户记住的品牌承诺。作为睡前和专注类应用，首屏、商店描述和信任文案需要更明确地传达“低打扰、本地、安静、可离线”的差异化，同时避免医疗化、玄学化或过度承诺。

## What Changes

- 建立 WhiteNoisePro 的品牌文案方向：安静、克制、有一点诗意，但不承诺治疗或保证入睡。
- 引入主 slogan 候选，优先使用“把世界调低一点。”作为首屏和商店文案的核心记忆点。
- 强化 Home、推荐定时、声音库、Settings 和 Google Play listing draft 的文案一致性。
- 修正 Settings 的声音来源说明，使其反映当前 19 个本地声音：第一方噪声 + 已处理 CC0 自然录音。
- 保持功能、导航、音频播放、隐私实际行为不变。

## Capabilities

### New Capabilities

- `brand-copy`: 定义品牌语气、主 slogan、应用内关键文案和安全边界。

### Modified Capabilities

- `settings-trust`: Settings 信任文案必须准确反映当前声音来源，不再只说第一方程序化生成音频。
- `release-readiness`: Google Play listing 草案必须包含更有记忆点且政策安全的品牌文案。

## Impact

- 影响应用内展示文案：Home、推荐定时、Library section、Settings。
- 影响 release-readiness 文档：store listing draft、可能的截图说明和 closed testing 文案。
- 需要新增或更新文案测试，避免误用医疗承诺、广告化措辞或不准确的声音来源描述。
- 不新增 SDK、网络能力、账号、analytics、广告或音频资源。
