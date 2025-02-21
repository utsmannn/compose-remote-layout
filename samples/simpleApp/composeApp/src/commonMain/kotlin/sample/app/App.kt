package sample.app

import androidx.compose.runtime.Composable
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun App() {
    val jsonLayout = """
{
  "column": {
    "modifier": {
      "base": {
        "padding": {
          "all": 12
        }
      }
    },
    "children": [
      {
        "user": {
          "name": "Utsman",
          "phone": "0812345678"
        }
      }
    ]
  }
}
    """

    val component = createLayoutComponent(jsonLayout)
    DynamicLayout(component = component)
}
