// Versões ALINHADAS ao VaultCam e ao placa-check (que já compilam no seu Android Studio):
// AGP 8.7.0 + Kotlin 2.0.21 + compose plugin 2.0.21. Único acréscimo: KSP (pro Room).
plugins {
    id("com.android.application") version "8.7.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
