import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import sample.app.App

fun MainViewController(): UIViewController {
    /*CustomNodes.register("banner") { param ->
        Column(
            modifier = param.modifier,
        ) {
            Text(
                text = param.data["title"] ?: "unknown",
                style = MaterialTheme.typography.h2,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = param.data["message"] ?: "unknown",
            )
        }
    }*/
    return ComposeUIViewController { App() }
}
