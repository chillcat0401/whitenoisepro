plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21" apply false
    id("org.jetbrains.compose") version "1.11.1" apply false
}

tasks.register<Exec>("verifyArchiveAcceptance") {
    group = "verification"
    description = "Verifies acceptance evidence for every archived OpenSpec change."
    workingDir = rootDir
    commandLine(
        "node",
        "tools/verify_archive_acceptance.mjs",
        "--all-archives",
    )
}

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named("verifyArchiveAcceptance"))
    }
}
