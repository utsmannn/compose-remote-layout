@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("ktlint:standard:filename")

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import sample.app.App

@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController {
    val jsonDefault = """
{
  "column": {
    "modifier": {
      "base": {
        "width": 200,
        "padding": {
          "all": 16
        }
      },
      "verticalArrangement": "spaceBetween",
      "horizontalAlignment": "center"
    },
    "children": [
      {
        "button": {
          "content": "Click me",
          "clickId": "button1",
          "modifier": {
            "base": {
              "fillMaxWidth": true
            }
          }
        }
      },
      {
        "text": {
          "content": "Hello From ios",
          "modifier": {
            "base": {
              "padding": {
                "top": 8
              }
            }
          }
        }
      }
    ]
  }
}
    """.trimIndent()

    return ComposeUIViewController {
        var layoutJson by remember { mutableStateOf(jsonDefault) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            FIRApp.configure()
            val remoteConfig = FIRRemoteConfig.remoteConfig()
            remoteConfig.configSettings = FIRRemoteConfigSettings().also {
                it.minimumFetchInterval = 1.0
            }

            remoteConfig.setDefaults(
                mapOf("layout" to jsonDefault),
            )

            remoteConfig.configValueForKey("layout").also {
                layoutJson = it.stringValue
            }


            remoteConfig.addOnConfigUpdateListener { configUpdate, nsError ->
                if (configUpdate != null) {                
                    remoteConfig.fetchAndActivateWithCompletionHandler { firRemoteConfigFetchAndActivateStatus, nsError ->                        println("cuaks fetchAndActivate... --> ${nsError?.localizedDescription}")
                        remoteConfig.configValueForKey("layout").also {
                            layoutJson = it.stringValue
                        }
                    }
                }
            }
        }

        App(layoutJson)
    }
}
