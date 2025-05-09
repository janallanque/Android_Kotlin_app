plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "br.com.alura.orgs"
    compileSdk = 36
    ndkVersion = "36.0.0"
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "br.com.alura.orgs"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
            isCrunchPngs = true
        }
        debug {
            isCrunchPngs = false
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Verifique a versão mais recente
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}


dependencies {
    // Room
    ksp(libs.androidx.room.room.compiler2)
    implementation(libs.androidx.room.ktx)

    // KSP
    ksp(libs.symbol.processing)
    implementation(libs.symbol.processing.api)
    annotationProcessor(libs.androidx.room.room.compiler2)

    // Kotlin
    implementation(libs.kotlin.stdlib)

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.legacy.support.v13)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.annotation)

    // Material
    implementation(libs.material)
    implementation(libs.androidx.material3)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.gif.v260)

    // Outros
    implementation(libs.google.filament.android)
    implementation(libs.firebase.database.ktx)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
