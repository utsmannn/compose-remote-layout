plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    js(IR) {
        browser()
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
    }
}
