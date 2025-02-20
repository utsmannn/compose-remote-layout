import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm()
    js {
        browser()
        binaries.executable()
    }
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

    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        podfile = project.file("../iosApp/Podfile")
        ios.deploymentTarget = "17.0"

        name = "ComposeApp"

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("FirebaseAnalytics") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseRemoteConfig") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseCoreInternal") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseCore") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material)
            implementation(projects.composeRemoteLayout)
            implementation(projects.samples.sharedCompose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "sample.firebase.app"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        applicationId = "sample.firebase.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sample.firebase"
            packageVersion = "1.0.0"
        }
    }
}
