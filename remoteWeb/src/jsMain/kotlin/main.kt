@file:Suppress("ktlint:standard:filename")

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.seiko.imageloader.rememberImagePainter
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("ktlint:standard:backing-property-naming")
private var _textJson = MutableStateFlow("{}")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.getElementById("compose") ?: return

    CustomNodes.register("banner") { param ->
        Column(
            modifier = param.modifier,
        ) {
            Text(
                text = param.data["title"] ?: "unknown",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = param.data["message"] ?: "unknown",
            )
        }
    }

    CustomNodes.register("image") { param ->
        val url = param.data["url"] ?: ""
        if (url.isNotEmpty()) {
            val painter = rememberImagePainter(url)
            Image(
                painter = painter,
                contentDescription = "image",
                modifier = param.modifier,
                contentScale = ContentScale.FillWidth,
            )
        }
    }

    ComposeViewport(body) {
        val textJson by _textJson.collectAsState()
        val layoutNode by remember { derivedStateOf { parseLayoutJson(textJson) } }

        var counter by remember { mutableStateOf(0) }
        val bindsValue = remember { BindsValue() }

        LaunchedEffect(counter) {
            bindsValue.setValue("counter", counter)
        }

        DynamicLayout(
            component = layoutNode,
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

@OptIn(ExperimentalJsExport::class)
@JsExport
fun updateEditorContent(content: String) {
    _textJson.value = content
}
