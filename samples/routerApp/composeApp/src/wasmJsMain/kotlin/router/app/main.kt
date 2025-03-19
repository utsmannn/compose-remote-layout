import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import router.app.App
import shared.compose.Shared

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Shared.registerCustomNode()
    val body = document.body ?: return
    ComposeViewport(body) {
        App()
    }
}
