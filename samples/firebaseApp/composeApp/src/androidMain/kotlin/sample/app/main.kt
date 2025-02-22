package sample.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import shared.compose.Shared

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(
            mapOf(remoteConfigKey to jsonDefault),
        )

        Shared.registerCustomNode()

        setContent {
            var layoutJson by remember { mutableStateOf(jsonDefault) }

            LaunchedEffect(Unit) {
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    remoteConfig.getString(remoteConfigKey).also {
                        layoutJson = it
                    }
                }

                remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                    override fun onUpdate(configUpdate: ConfigUpdate) {
                        remoteConfig.fetchAndActivate().addOnCompleteListener {
                            remoteConfig.getString(remoteConfigKey).also {
                                layoutJson = it
                            }
                        }
                    }

                    override fun onError(error: FirebaseRemoteConfigException) {
                        error.printStackTrace()
                    }
                })
            }

            App(layoutJson)
        }
    }
}
