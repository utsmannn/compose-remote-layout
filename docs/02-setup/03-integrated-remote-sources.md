### 1. Integrated with your API

In a real-world application, you'll typically load layouts from a remote source. Here's an example using a simple API call:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RemoteLayout() {
    var layoutJson by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Replace with your actual API call
            layoutJson = fetchLayoutFromApi()
            isLoading = false
        }
    }
    
    if (!isLoading && layoutJson.isNotEmpty()) {
        val component = createLayoutComponent(layoutJson)
        DynamicLayout(component = component)
    } else {
        // Show loading state
    }
}

suspend fun fetchLayoutFromApi(): String {
    // Implement your API call here
    return ""
}
```

### 2. Firebase Remote Config Integration

For a more practical approach, you can use Firebase Remote Config to deliver layout updates:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun FirebaseRemoteLayout(remoteConfig: FirebaseRemoteConfig) {
    var layoutJson by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the layout JSON from Remote Config
                layoutJson = remoteConfig.getString("layout_json")
            }
        }
    }
    
    if (layoutJson.isNotEmpty()) {
        val component = createLayoutComponent(layoutJson)
        DynamicLayout(component = component)
    } else {
        // Show default layout or loading state
    }
}
```