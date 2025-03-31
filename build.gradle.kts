plugins {
    // Esse bloco define quais plugins serão usados neste projeto.
    // A versão do plugin android precisa ser especificada no classpath.
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false // Aqui
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" // this version matches your Kotlin version
}

buildscript {
    // Este bloco configura o classpath para o plugin do Android.
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Aqui definimos o classpath para o plugin android.
        classpath(libs.gradle) // Substitua 8.x.x pela versão mais recente
        classpath(libs.kotlin.gradle.plugin) // Exemplo de versão do Kotlin

    }

}

tasks.register("clean", Delete::class) {
    // Define uma task "clean" para remover o diretório de build do projeto.
    delete(rootProject)
}