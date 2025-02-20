plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

val ktorVersion = "3.1.0"

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm()
    js { browser() }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material)

            api("io.github.qdsfdhvh:image-loader:1.10.0")
            api(projects.composeRemoteLayout)
        }
    }
}

android {
    namespace = "sample.sharedcompose"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }
}
