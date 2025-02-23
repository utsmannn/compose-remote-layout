import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    id("convention.publication")
}

// Package.swift
// // swift-tools-version:5.3
// import PackageDescription
//
// let package = Package(
//   name: "ComposeRemoteLayout",
//   platforms: [
//     .iOS(.v14),
//   ],
//   products: [
//      .library(name: "ComposeRemoteLayout", targets: ["ComposeRemoteLayout"])
//   ],
//   targets: [
//      .binaryTarget(
//         name: "ComposeRemoteLayout",
//         path: "./ComposeRemoteLayout.xcframework")
//   ]
// )

kotlin {
    jvmToolchain(17)

    val xcframeworkName = "ComposeRemoteLayout"
    val xcf = XCFramework(xcframeworkName)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js { browser() }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = xcframeworkName

            // Specify CFBundleIdentifier to uniquely identify the framework
            binaryOption("bundleId", "com.utsman.composeremote.$xcframeworkName")
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }
}

android {
    namespace = "com.utsman.composeremote.app"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}
