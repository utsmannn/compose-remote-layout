import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "sampleRouterApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        open = false
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                    port = 8083
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
            implementation(projects.composeRemoteLayout)
            implementation(projects.composeRemoteLayoutRouter)
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
    namespace = "router.app"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        applicationId = "router.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sample.router"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<Copy>("copyWasmJsToRoot") {
    description = "Copy WasmJs build output to root project directory"
    group = "build"

    dependsOn("wasmJsBrowserDevelopmentExecutableDistribution")

    val destFolder = file("${project.projectDir}/../wasmJsDist")
    if (!destFolder.exists()) {
        println("Creating wasmJsDist folder ...")
        destFolder.mkdirs()
    }

    println("Copying wasmJs build output to wasmJsDist directory ...")
    from("${layout.buildDirectory.get().asFile}/dist/wasmJs/developmentExecutable")
    into(destFolder)
}

tasks.named("wasmJsBrowserDevelopmentExecutableDistribution").configure {
    finalizedBy("copyWasmJsToRoot")
}
