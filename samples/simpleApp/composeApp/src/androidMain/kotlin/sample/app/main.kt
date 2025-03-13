package sample.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import shared.compose.Shared

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shared.registerCustomNode()

        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(
                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                            )
                            .padding(innerPadding),
                    ) {
                        App()
                    }
                }
            }
        }
    }
}
