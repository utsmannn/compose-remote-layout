plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlinCocoapods).apply(false)
    id("com.google.gms.google-services") version "4.4.2" apply false
}
