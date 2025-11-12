// File: app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    // [FIX] Namespace dan App ID disesuaikan dengan package Anda
    namespace = "id.antasari.p6minda_230104040129" // [cite: 142]
    compileSdk = 34 // [cite: 142]

    defaultConfig {
        applicationId = "id.antasari.p6minda_230104040129" // [cite: 146]
        minSdk = 24 // [cite: 147]
        targetSdk = 34 // [cite: 148]
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true // [cite: 161]
    }

    // Samakan Java/Kotlin ke versi 17 (Wajib untuk Room & Compose modern)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // [cite: 166, 169]
        targetCompatibility = JavaVersion.VERSION_17 // [cite: 167, 170]
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    //===== COMPOSE =====
    // [BOM (Bill of Materials) untuk menyamakan versi compose]
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00") // [cite: 179]
    implementation(composeBom) // [cite: 180]
    androidTestImplementation(composeBom) // [cite: 181]
    // [Komponen Inti UI Compose]
    implementation("androidx.compose.ui:ui") // [cite: 183]
    implementation("androidx.compose.material3:material3") // [cite: 184]
    implementation("androidx.compose.ui:ui-tooling-preview") // [cite: 184]
    debugImplementation("androidx.compose.ui:ui-tooling") // [cite: 184]
    // [Integrasi Compose dengan Activity & Lifecycle]
    implementation("androidx.activity:activity-compose:1.9.2") // [cite: 186]
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6") // [cite: 188]
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6") // [cite: 189]
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6") // [cite: 190]

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //===== ROOM (local DB offline / CRUD) =====
    val roomVersion = "2.6.1" // [cite: 192]
    implementation("androidx.room:room-runtime:$roomVersion") // [cite: 193]
    implementation("androidx.room:room-ktx:$roomVersion") // [cite: 193]
    ksp("androidx.room:room-compiler:$roomVersion") //

    //===== Coroutines (Untuk menjalankan database di background thread) =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1") // [cite: 196]
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}