package sample.app

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson

@Suppress("ktlint:standard:function-naming")
@Composable
fun App() {
    val textJson =
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
          "text": "Click me",
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
          "text": "{counter}",
          "modifier": {
            "base": {
              "padding": {
                "top": 8
              }
            }
          }
        }
      },
      {
        "text": {
            "text": "Click me"
        }
      },{
        "banner": {
            "title": "hjabsjkhskjashkjashjlkas"
        }
      }
    ]
  }
}
        """.trimIndent()

    Scaffold {
        val layoutNode by remember { mutableStateOf(parseLayoutJson(textJson)) }
//
        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }
    }
}
