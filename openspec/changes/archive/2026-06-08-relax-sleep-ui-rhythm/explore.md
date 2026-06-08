# Explore: 睡眠 UI 呼吸感升级

## 当前观察

上一轮已经补齐现代化 icon 与更丰富的声音素材，但布局仍显得偏紧凑，主要来自以下代码事实：

- 全局横向边距为 16dp，顶部 content padding 为 16dp。
- Mini Player 高 64dp，底部导航高 64dp，屏幕底部两块控件贴得较近。
- 多数列表间距使用 12dp 或 16dp，卡片内部 padding 多为 12dp。
- Home 的 hero、最近使用、推荐声音都希望在首屏出现，导致层级不够松弛。
- Library 声音卡片使用较小 aspect ratio，信息密度偏工具型。

## 方案比较

### 方案 A：整体放大和留白

增加 screen horizontal padding、top padding、bottom safe padding、卡片内边距、列表间距和 Mini Player 高度。

优点：

- 改动最集中，风险低。
- 能直接缓解“挤”的主观感受。

风险：

- 小屏幕上首屏内容变少，用户需要更多滚动。

### 方案 B：首页沉浸式重排

将 Home 改成更大 hero、更少首屏模块，只保留播放核心，其他模块下移。

优点：

- 首页最惬意，最像睡眠应用。

风险：

- 变更面更大，可能削弱 Library / Mixer 等工具页的一致节奏。

### 方案 C：局部调整单个控件

只增大 hero、Mini Player 或声音卡片。

优点：

- 改动很小。

风险：

- 只能解决局部观感，整体仍会紧。

## 推荐

采用方案 A，并在 Home 和 Library 上做少量结构化补强：

- 新增 comfort spacing tokens，而不是散落硬编码。
- 屏幕左右边距从 16dp 提升到 20dp，顶部从 16dp 提升到 24dp。
- 列表节奏从 12-16dp 提升到 20-24dp。
- 卡片内边距统一向 16-24dp 靠拢。
- Mini Player 增高到 76dp，底部区域更像悬浮控制条。
- Home hero 内部留白增加，SoundIcon 和控制按钮之间更舒缓。
- Library 卡片更高，声音图标和文字有更明确层级。

这个方案保留“工具可用性”，但让睡前使用的节奏慢下来。

