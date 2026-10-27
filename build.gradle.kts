// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // A versão tem que ser a mesma do Kotlin do projeto (libs.versions.toml).
    // Kotlin 2.4.20 porque o maps-compose 9.0.0 foi compilado com Kotlin 2.4.
    kotlin("plugin.serialization") version "2.4.20" apply false
}
