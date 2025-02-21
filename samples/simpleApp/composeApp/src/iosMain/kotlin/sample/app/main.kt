import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.utsman.composeremote.CustomNodes
import platform.UIKit.UIViewController
import sample.app.App

fun MainViewController(): UIViewController {
    CustomNodes.register("user") { param ->
        Column(
            modifier = param.modifier,
        ) {
            Text(
                text = param.data["name"] ?: "unknown",
                style = MaterialTheme.typography.body1,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = param.data["phone"] ?: "unknown",
                style = MaterialTheme.typography.caption,
            )
        }
    }

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
