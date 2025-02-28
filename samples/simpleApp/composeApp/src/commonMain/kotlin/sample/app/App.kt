package sample.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun App() {
    var jsonLayout by remember {
        mutableStateOf(
            """
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
        "text": {
          "content": "Counter: {counter}"
        }
      },
      {
        "cuaks": {
            "title": "cuk"
        }
      }
    ]
  }
}
    """,
        )
    }

    val component = createLayoutComponent(jsonLayout)
    val bindsValue = remember { BindsValue() }

    var counter by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        while (true) {
            scope.launch {
                counter++
            }
            delay(1000)
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        jsonLayout = """
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
        "text": {
          "content": "Counter: {counter}"
        }
      },
      {
        "cuaks": {
            "title": "anjay mamen"
        }
      }
    ]
  }
}
    """
    }

    LaunchedEffect(counter) {
        bindsValue.setValue("counter", counter)
    }

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
    )
}
