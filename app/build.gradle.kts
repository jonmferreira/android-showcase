plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

// Base alinhada ao placa-check (último validado OK): compileSdk 35, minSdk 26, Java 21,
// CameraX 1.3.4, ML Kit 16.0.1, coroutines-play-services 1.8.1.
// Compose alinhado ao VaultCam (mesmo AGP 8.7.0 / Kotlin 2.0.21): BOM 2024.12.01.
android {
    namespace = "com.jonathan.showcase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jonathan.showcase"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // CameraX (mesma versão do placa-check/VaultCam)
    val camerax = "1.3.4"
    implementation("androidx.camera:camera-core:$camerax")
    implementation("androidx.camera:camera-camera2:$camerax")
    implementation("androidx.camera:camera-lifecycle:$camerax")
    implementation("androidx.camera:camera-view:$camerax")

    // ML Kit on-device (text = mesma do placa-check)
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    // Room (offline-first) — acréscimo, via KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // 3D nativo (Filament via SceneView, Compose). Versão 2.2.1 = compatível com AGP 8.x.
    // Se o Gradle sync reclamar de tooling/versão, ajustar aqui (ver skill android-3d-native).
    implementation("io.github.sceneview:sceneview:2.2.1")

    // WebView + AssetLoader (home = stage web Three.js servido via https virtual → ESM sem CORS)
    implementation("androidx.webkit:webkit:1.12.1")

    // QR code (gerar o QR da release do APK na home)
    implementation("com.google.zxing:core:3.5.3")

    // Jetpack Compose (do VaultCam)
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
}
