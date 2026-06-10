/**
 * 标准构建文件(默认 settings,Kotlin 2.0.21):仅保留 androidTarget,
 * 用途 = 共享层单元测试与 Android 侧验证。鸿蒙产物走 build.2.0.ohos.gradle.kts。
 * 自 demo/build.gradle.kts 派生裁剪:移除 js/ios/macos/cocoapods/core-wx/ktor。
 */
plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(project(":compose"))
                implementation(project(":core-annotations"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1-KBA-003")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0-KBA-002")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
    }
}

dependencies {
    compileOnly(project(":core-ksp")) {
        add("kspAndroid", this)
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.whitenoisepro.kuikly"
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
}
