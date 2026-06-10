## Context

WhiteNoisePro 当前已有内部生成工具，但自然雨声听感未达到产品目标。真实录音素材能更快获得可信的自然纹理，但也带来版权、来源证明和音频处理风险。

## Goals / Non-Goals

**Goals:**

- 确定可商用上架的外部音频渠道。
- 建立种子文件候选列表。
- 定义证据和准入字段，避免版权不清的素材进入发布包。

**Non-Goals:**

- 本变更不下载和提交大音频文件。
- 不把任何外部素材加入发布包。
- 不承诺第三方平台所有文件都安全，只对逐条验收后的文件放行。

## Decisions

### 1. 优先使用 CC0 单文件

CC0 文件最适合早期上架：无需 attribution，修改和商用限制最低，证据链也最简单。

### 2. BBC 免费库列为禁用，除非购买商业授权

BBC 免费库适合探索和参考，不适合直接进入商业 app。若确实要使用，必须先购买单条商业授权。

### 3. 建立 intake 目录而不是直接入包

外部音频应先进入 `work/audio-intake/` 或文档候选清单，通过 license、hash、听测、loop、loudness 和包体检查后再另开发布变更。

## Risks / Trade-offs

- [Risk] 免费平台存在上传者误标 license。  
  [Mitigation] 优先使用 CC0、保留页面证据、避免明显来自 BBC/影视/商业库的二次上传。

- [Risk] 音频长、体积大。  
  [Mitigation] 后续发布变更中统一转码、裁剪、loop 和 loudness normalize。
