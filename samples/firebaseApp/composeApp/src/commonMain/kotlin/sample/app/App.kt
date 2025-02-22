package sample.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson

@Suppress("ktlint:standard:function-naming")
@Composable
fun App(textJson: String = "{}") {
    var counter by remember { mutableStateOf(0) }
    val bindsValue = remember { BindsValue() }

    LaunchedEffect(counter) {
        bindsValue.setValue("counter", counter)
    }

    Scaffold(
        modifier = Modifier
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            ),
    ) {
        val layoutNode by remember(textJson) { mutableStateOf(parseLayoutJson(textJson)) }

        DynamicLayout(component = layoutNode, bindValue = bindsValue) { clickId ->
            when (clickId) {
                "button1" -> {
                    counter++
                }
            }
        }
    }
}

const val remoteConfigKey = "home"

val jsonDefault = """
{
  "column": {
    "modifier": {
      "base": {
        "width": 200,
        "padding": {
          "all": 16
        }
      },
      "verticalArrangement": "spaceBetween",
      "horizontalAlignment": "center"
    },
    "children": [
      {
        "button": {
          "content": "Click me",
          "clickId": "button1",
          "modifier": {
            "base": {
              "fillMaxWidth": true
            }
          }
        }
      },
      {
        "text": {
          "content": "Hello From ios",
          "modifier": {
            "base": {
              "padding": {
                "top": 8
              }
            }
          }
        }
      }
    ]
  }
}
""".trimIndent()
