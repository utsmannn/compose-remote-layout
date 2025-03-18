import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "jsonBuilderWeb"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "jsonBuilderWeb.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        open = false
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                    port = 8082
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.composeRemoteLayout)
            implementation(projects.samples.sharedCompose)

            implementation(compose.ui)
            implementation(compose.material3)

            api("io.github.qdsfdhvh:image-loader:1.10.0")
        }

//        wasmJsMain.dependencies {
//            implementation(npm("path-browserify", "1.0.1"))
//        }
    }
}

tasks.register<Copy>("copyWasmJsToRoot") {
    description = "Copy WasmJs build output to root project directory"
    group = "build"

    dependsOn("wasmJsBrowserDevelopmentExecutableDistribution")

    val destFolder = file("${project.rootDir}/wasmJsDist")
    if (!destFolder.exists()) {
        println("Creating wasmJsDist folder ...")
        destFolder.mkdirs()
    }

    println("Copying wasmJs build output to wasmJsDist directory ...")
    from("$buildDir/dist/wasmJs/developmentExecutable")
    into("${project.rootDir}/wasmJsDist")
}

tasks.named("wasmJsBrowserDevelopmentExecutableDistribution").configure {
    finalizedBy("copyWasmJsToRoot")
}
