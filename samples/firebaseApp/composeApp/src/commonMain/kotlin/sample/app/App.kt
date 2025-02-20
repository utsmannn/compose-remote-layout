package sample.app

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson

val textJsonDefault =
    """
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
          "content": "Hello World",
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun App(textJson: String = textJsonDefault) {
    Scaffold {
        val layoutNode by remember(textJson) { mutableStateOf(parseLayoutJson(textJson)) }

        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }
    }
}
