import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    id("convention.publication")
}

group = "io.github.utsmannn"
version = project.findProperty("version")?.toString() ?: "0.0.1"

mavenPublish {
    artifactId = "compose-remote-layout"
    moduleName = "Compose Remote Layout"
    moduleDescription = "Compose Remote Layout is a library to create dynamic layout from remote json"
}

kotlin {
    jvmToolchain(17)

    val xcframeworkName = "ComposeRemoteLayoutCore"
    val xcf = XCFramework(xcframeworkName)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeRemoteLayout"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeRemoteLayout.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
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

            implementation(libs.kotlinx.datetime)
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
