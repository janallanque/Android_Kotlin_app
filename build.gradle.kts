@file:Suppress("DEPRECATION")

plugins {
    id("com.android.application") version "8.4.2" apply false
    id("com.android.library") version "8.4.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle) // Versão fixa aqui
        classpath(libs.kotlin.gradle.plugin) // Versão fixa aqui
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}