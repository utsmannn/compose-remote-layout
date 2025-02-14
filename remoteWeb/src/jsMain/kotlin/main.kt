import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow

private var _textJson = MutableStateFlow("{}")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.getElementById("compose") ?: return
    ComposeViewport(body) {
        val textJson by _textJson.collectAsState()
        val layoutNode by remember { derivedStateOf { parseLayoutJson(textJson) } }

        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun updateEditorContent(content: String) {
    _textJson.value = content
}