import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import com.utsman.composeremote.StateContainer
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow

private var _textJson = MutableStateFlow("{}")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.getElementById("compose") ?: return
    ComposeViewport(body) {
        val textJson by _textJson.collectAsState()
        val layoutNode by remember { derivedStateOf { parseLayoutJson(textJson) } }

        var counter by remember { mutableStateOf(0) }

//        val stateContainer = remember(counter) { StateContainer(state = mutableMapOf("counter" to counter)) }

        DynamicLayout(
            component = layoutNode,
        ) { clickId ->
            when (clickId) {
                "button1" -> counter++
            }
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun updateEditorContent(content: String) {
    _textJson.value = content
}