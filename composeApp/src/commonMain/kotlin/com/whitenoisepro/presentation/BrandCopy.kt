package com.whitenoisepro.presentation

object BrandCopy {
    const val PrimarySlogan = "把世界调低一点。"
    const val HomeSupportingLine = "把世界调低一点，留一层刚好的声音。"
    const val RecommendedSoundsTitle = "适合今晚的声音"
    const val BedtimeTimerTitle = "今晚的淡出定时"
    const val SettingsAudioSourceSubtitle = "本地打包声音，包含第一方噪声与已处理的 CC0 自然录音"

    private val policySensitiveClaims = listOf(
        "治疗",
        "治愈",
        "改善失眠",
        "保证入睡",
        "医学级",
        "临床",
    )

    fun bedtimeTimerSubtitle(minutes: Int): String =
        "$minutes 分钟后慢慢安静下来"

    fun hasPolicySensitiveClaim(copy: String): Boolean =
        policySensitiveClaims.any(copy::contains)
}
