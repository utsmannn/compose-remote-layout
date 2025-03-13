import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import sample.app.App
import shared.compose.Shared

fun MainViewController(): UIViewController {
    Shared.registerCustomNode()

    return ComposeUIViewController {
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
