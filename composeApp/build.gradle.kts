import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
}

val localSigningProperties = Properties().apply {
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(::load)
    }
}

fun signingValue(environmentName: String, propertyName: String): String? =
    providers.environmentVariable(environmentName).orNull
        ?.takeIf(String::isNotBlank)
        ?: localSigningProperties.getProperty(propertyName)?.takeIf(String::isNotBlank)

val releaseSigningValues = mapOf(
    "storeFile" to signingValue("WHITENOISE_UPLOAD_STORE_FILE", "storeFile"),
    "storePassword" to signingValue("WHITENOISE_UPLOAD_STORE_PASSWORD", "storePassword"),
    "keyAlias" to signingValue("WHITENOISE_UPLOAD_KEY_ALIAS", "keyAlias"),
    "keyPassword" to signingValue("WHITENOISE_UPLOAD_KEY_PASSWORD", "keyPassword"),
)
val configuredSigningValueCount = releaseSigningValues.values.count { it != null }
check(configuredSigningValueCount == 0 || configuredSigningValueCount == releaseSigningValues.size) {
    "Release signing is partially configured. Provide all four WHITENOISE_UPLOAD_* variables " +
        "or all four entries from keystore.properties.example."
}
val hasReleaseSigning = configuredSigningValueCount == releaseSigningValues.size
val releaseArtifactTasks = setOf(
    "assembleRelease",
    "build",
    "bundleRelease",
    "packageRelease",
    "validateSigningRelease",
)
val releaseArtifactRequested = gradle.startParameter.taskNames
    .map { it.substringAfterLast(':') }
    .any(releaseArtifactTasks::contains)

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
        }
        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.12.0")
            implementation("androidx.datastore:datastore-preferences:1.2.1")
            implementation("androidx.media3:media3-exoplayer:1.10.1")
            implementation("androidx.media3:media3-session:1.10.1")
        }
        androidUnitTest.dependencies {
            implementation("androidx.datastore:datastore-preferences:1.2.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
        }
    }
}

android {
    namespace = "com.whitenoisepro"
    compileSdk = 36

    defaultConfig {
        // Compose 资源(字体等)生成类的包名在下方 compose.resources 配置
        applicationId = "com.whitenoisepro"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.2.0"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("releaseUpload") {
                storeFile = rootProject.file(releaseSigningValues.getValue("storeFile")!!)
                storePassword = releaseSigningValues.getValue("storePassword")
                keyAlias = releaseSigningValues.getValue("keyAlias")
                keyPassword = releaseSigningValues.getValue("keyPassword")
            }
        }
    }

    buildTypes {
        getByName("release") {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("releaseUpload")
            }
        }
    }
}

check(!releaseArtifactRequested || hasReleaseSigning) {
    "Release signing is not configured. Copy keystore.properties.example to keystore.properties " +
        "or provide all four WHITENOISE_UPLOAD_* environment variables."
}

compose.resources {
    packageOfResClass = "com.whitenoisepro.generated.resources"
}
