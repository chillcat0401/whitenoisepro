package com.whitenoisepro.app

/**
 * 自主仓 app/AppShell.kt 抽取的纯枚举(UI 部分 M3 翻译时再并入)。
 */
enum class AppTab(val title: String) {
    Home("首页"),
    Mixer("混音"),
    Library("声音"),
    Timer("定时"),
    Saved("已保存"),
    Settings("设置"),
    ;

    companion object {
        val bottomNavTabs: List<AppTab> = listOf(Home, Mixer, Library, Timer, Saved)
    }
}
