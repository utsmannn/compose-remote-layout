package sample.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import shared.compose.Shared

@Suppress("ktlint:standard:function-naming")
@Composable
fun App() {
    Shared.registerCustomNode()

    val textJson =
        """
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
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
      },
      {
        "banner": {
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "height": 120
            }
          },
          "title": "uhuy",
          "message": "Beli lah uhuy",
          "url": "https://cdn.onemars.net/sites/sheba_id_xGoUJ_pZtz/image/list-sheba46_1709897936345.webp"
        }
      },
      {
        "image": {
          "modifier": {
            "base": {
              "width": 200,
              "height": 200
            }
          },
          "url": "https://cdn.onemars.net/sites/sheba_id_xGoUJ_pZtz/image/list-sheba46_1709897936345.webp"
        }
      }
    ]
  }
}
        """.trimIndent()

    MaterialTheme {
        val layoutNode by remember { mutableStateOf(parseLayoutJson(textJson)) }
//
        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }
    }
}
