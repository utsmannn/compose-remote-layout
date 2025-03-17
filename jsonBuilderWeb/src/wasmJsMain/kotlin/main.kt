@file:Suppress("ktlint:standard:filename")

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import shared.compose.Shared

@Suppress("ktlint:standard:backing-property-naming")
private var _textJson = MutableStateFlow("{}")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.getElementById("compose") ?: return

    Shared.registerCustomNode()

    ComposeViewport(body) {
        val textJson by _textJson.collectAsState()

        var counter by remember { mutableStateOf(0) }
        val bindsValue = remember { BindsValue() }

        LaunchedEffect(counter) {
            bindsValue.setValue("counter", counter)
        }

        MaterialTheme {
            DynamicLayout(
                component = createLayoutComponent(textJson),
                bindValue = bindsValue,
            ) { clickId ->
                when (clickId) {
                    "button1" -> {
                        counter++
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun updateEditorContent(content: String) {
    _textJson.value = content
}
