# 外部音频来源探索

## 结论

内部程序化生成工具已完成并归档，但当前听感达不到最终产品质量要求。下一阶段应改为外部真实录音素材 intake：先找可商用、可上架、可留存证据的来源，再建立种子文件清单。

## 来源渠道判断

- Freesound CC0：优先渠道。单文件许可证明确，适合保留原始 URL、作者、license、hash 和下载时间。
- Sonniss GDC Bundle：次优渠道。EULA 覆盖商业交互项目，但 bundle 体积大，需要本地筛选自然环境长录音。
- Pixabay / Signature Sounds / BigSoundBank / OpenGameArt：可作为补充渠道，但每条素材仍需要单独截图或保存 license 页面。
- BBC Sound Effects：不作为免费来源。免费 RemArc 许可只适合个人、教育或研究；商用需要通过 Pro Sound Effects 授权。

## 推荐

先建立 8-12 个“种子音频”候选，集中覆盖小雨、中雨/雨幕、海浪、火炉、森林、风扇/机械底噪。候选只进入 `work/audio-intake/`，不直接进发布包。
