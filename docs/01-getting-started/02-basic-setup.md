Let's start with a basic layout that displays a text element:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun SimpleLayout() {
    val jsonLayout = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 16
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Hello from Remote Layout!",
              "fontSize": 20,
              "fontWeight": "bold",
              "color": "#0066CC"
            }
          }
        ]
      }
    }
    """
    
    val component = createLayoutComponent(jsonLayout)
    
    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            // We'll handle clicks later
        }
    )
}
```
