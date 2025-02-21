@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("ktlint:standard:filename")

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import sample.app.App
import sample.app.jsonDefault
import sample.app.remoteConfigKey

@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController = ComposeUIViewController {
    var layoutJson by remember { mutableStateOf(jsonDefault) }

    LaunchedEffect(Unit) {
        FIRApp.configure()
        val remoteConfig = FIRRemoteConfig.remoteConfig()
        remoteConfig.configSettings = FIRRemoteConfigSettings().also {
            it.minimumFetchInterval = 1.0
        }

        remoteConfig.setDefaults(
            mapOf(remoteConfigKey to jsonDefault),
        )

        remoteConfig.configValueForKey(remoteConfigKey).also {
            layoutJson = it.stringValue
        }

        remoteConfig.addOnConfigUpdateListener { configUpdate, _ ->
            if (configUpdate != null) {
                remoteConfig.fetchAndActivateWithCompletionHandler { _, _ ->
                    remoteConfig.configValueForKey(remoteConfigKey).also {
                        layoutJson = it.stringValue
                    }
                }
            }
        }
    }

    App(layoutJson)
}
