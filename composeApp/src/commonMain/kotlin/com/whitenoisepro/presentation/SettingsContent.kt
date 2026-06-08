package com.whitenoisepro.presentation

enum class SettingsRowKind {
    ReadOnly,
    Toggle,
    Disabled,
}

data class SettingsRowContent(
    val title: String,
    val subtitle: String,
    val kind: SettingsRowKind = SettingsRowKind.ReadOnly,
    val enabled: Boolean = true,
)

data class SettingsSectionContent(
    val title: String,
    val rows: List<SettingsRowContent>,
)

data class SettingsContent(
    val sections: List<SettingsSectionContent>,
) {
    companion object {
        fun releaseReady(): SettingsContent =
            SettingsContent(
                sections = listOf(
                    SettingsSectionContent(
                        title = "播放",
                        rows = listOf(
                            SettingsRowContent(
                                title = "主题",
                                subtitle = "深色，睡前低亮度优先",
                            ),
                            SettingsRowContent(
                                title = "音质",
                                subtitle = "标准，后续真实音频资产确定后开放选择",
                            ),
                            SettingsRowContent(
                                title = "启动时继续上次混音",
                                subtitle = "打开应用后恢复最近使用的助眠组合",
                                kind = SettingsRowKind.Toggle,
                            ),
                            SettingsRowContent(
                                title = "离线下载",
                                subtitle = "当前 MVP 使用本地打包声音，独立下载管理暂不可用",
                                kind = SettingsRowKind.Disabled,
                                enabled = false,
                            ),
                        ),
                    ),
                    SettingsSectionContent(
                        title = "隐私与权限",
                        rows = listOf(
                            SettingsRowContent(
                                title = "隐私政策",
                                subtitle = "无账号、无广告，偏好仅本地保存",
                            ),
                            SettingsRowContent(
                                title = "后台媒体控制",
                                subtitle = "Android 系统在播放时提供锁屏和通知栏控制，不用于营销通知",
                            ),
                        ),
                    ),
                    SettingsSectionContent(
                        title = "关于",
                        rows = listOf(
                            SettingsRowContent(
                                title = "版本",
                                subtitle = "0.1.0 MVP",
                            ),
                        ),
                    ),
                ),
            )
    }
}
