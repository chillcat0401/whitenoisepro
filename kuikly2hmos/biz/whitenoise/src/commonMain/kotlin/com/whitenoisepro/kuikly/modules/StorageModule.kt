package com.whitenoisepro.kuikly.modules

import com.tencent.kuikly.core.module.Module

/**
 * 键值持久化桥(common 侧定义,M2 实现)。
 *
 * 对照主仓 AppStorage(AndroidDataStoreAppStorage):只需要一个字符串槽位
 * 存 AppSnapshot JSON(AppSnapshotCodec 原样复用,旧 soundId 迁移逻辑随之生效)。
 * 鸿蒙侧用 @ohos.data.preferences 实现。
 *
 * 备注:Kuikly 自带 SharedPreferencesModule 可先用作 M1 过渡,本桥用于
 * 控制序列化时机与未来扩容(如导出/导入)。
 */
class StorageModule : Module() {

    override fun moduleName(): String = MODULE_NAME

    fun saveSnapshot(json: String) {
        toNative(false, "saveSnapshot", json, null, false)
    }

    fun loadSnapshot(callback: (json: String?) -> Unit) {
        toNative(
            false,
            "loadSnapshot",
            null,
            callbackFn = { data ->
                callback(data?.optString("json"))
            },
            false,
        )
    }

    companion object {
        const val MODULE_NAME = "WNPStorageModule"
    }
}
