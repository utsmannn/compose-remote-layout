One of the key advantages of Compose Remote Layout is its ability to load UI definitions from
multiple sources. This guide explores different methods for integrating remote layouts into your
application.

## The Power of Remote Layouts

The ability to update your UI without deploying a new app version offers significant benefits:

- **Rapid UI iterations**: Make UI changes instantly without app store review delays
- **A/B testing**: Test different layouts with different user segments
- **Seasonal UI**: Deploy special holiday or promotional interfaces on demand
- **Fix UI bugs**: Address layout issues without emergency releases
- **Feature flagging**: Gradually roll out new UI elements to select users
- **Device-specific layouts**: Deliver optimized experiences for different device types

## Source Flexibility

Compose Remote Layout is designed to be source-agnostic. The library only requires a JSON string -
how you obtain that string is entirely up to you. Here are the most common integration approaches:

### 1. API Integration

Fetch layouts from your backend API to deliver dynamic experiences:

```kotlin
@Composable
fun APILayout(
    layoutId: String = "home_screen",
    apiClient: LayoutApiClient,
    viewModel: ScreenViewModel
) {
    var layoutJson by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch layout when the composable is first displayed
    LaunchedEffect(layoutId) {
        try {
            // Get layout JSON from API
            layoutJson = apiClient.fetchLayout(layoutId)
            isLoading = false
        } catch (e: Exception) {
            // If fetch fails, use a fallback layout
            layoutJson = """
            {
              "column": {
                "children": [
                  {
                    "text": {
                      "content": "Could not load layout. Please check your connection.",
                      "textAlign": "center"
                    }
                  }
                ]
              }
            }
            """
            isLoading = false
        }
    }

    // Loading state
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Create component from JSON and render
        val component = createLayoutComponent(layoutJson)

        // Define bind values if needed
        val bindsValue = remember { BindsValue() }

        // Get data from viewModel and bind to layout
        val user by viewModel.currentUser.collectAsState()

        LaunchedEffect(user) {
            bindsValue.setValue("username", user.displayName)
            bindsValue.setValue("userLevel", user.level.toString())
        }

        // Render the dynamic layout
        DynamicLayout(
            component = component,
            bindValue = bindsValue,
            onClickHandler = { clickId ->
                viewModel.handleClick(clickId)
            }
        )
    }
}
```

### 2. Firebase Remote Config

Firebase Remote Config is perfect for A/B testing and gradual rollouts:

```kotlin
@Composable
fun FirebaseRemoteLayout(
    configKey: String = "home_layout",
    remoteConfig: FirebaseRemoteConfig
) {
    var layoutJson by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(configKey) {
        // Set minimum fetch interval
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour for production
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        val defaults = hashMapOf<String, Any>()
        defaults[configKey] = DEFAULT_LAYOUT_JSON
        remoteConfig.setDefaultsAsync(defaults)

        try {
            // Fetch config and activate it
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the layout JSON from Remote Config
                    layoutJson = remoteConfig.getString(configKey)
                } else {
                    // Use default if fetch fails
                    layoutJson = DEFAULT_LAYOUT_JSON
                }
                isLoading = false
            }
        } catch (e: Exception) {
            // Handle errors
            layoutJson = DEFAULT_LAYOUT_JSON
            isLoading = false
        }
    }

    if (isLoading) {
        // Show loading indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Create and render the component
        val component = createLayoutComponent(layoutJson)
        DynamicLayout(component = component)
    }
}

// Default layout as fallback
private const val DEFAULT_LAYOUT_JSON = """
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
          "content": "Welcome to our app!",
          "fontSize": 24,
          "fontWeight": "bold"
        }
      }
    ]
  }
}
"""
```

### 3. Local Assets

For bundling predefined layouts with your app:

```kotlin
@Composable
fun AssetBasedLayout(
    assetPath: String = "layouts/home_screen.json",
    context: Context = LocalContext.current
) {
    // Load layout from assets
    val layoutJson = remember(assetPath) {
        try {
            context.assets.open(assetPath)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            // Fallback if asset can't be loaded
            """{"text": {"content": "Could not load layout from assets"}}"""
        }
    }

    val component = createLayoutComponent(layoutJson)
    DynamicLayout(component = component)
}
```

### 4. Database Storage

For persisting and retrieving layouts from local storage:

```kotlin
@Composable
fun DatabaseLayout(
    layoutKey: String = "home_screen",
    layoutRepository: LayoutRepository
) {
    // Retrieve layout from the repository
    val layoutResult by produceState<Result<String>>(
        initialValue = Result.Loading,
        key1 = layoutKey
    ) {
        value = layoutRepository.getLayout(layoutKey)
    }

    when (layoutResult) {
        is Result.Loading -> {
            // Show loading state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Result.Success -> {
            // Create and render component
            val layoutJson = (layoutResult as Result.Success<String>).data
            val component = createLayoutComponent(layoutJson)
            DynamicLayout(component = component)
        }
        is Result.Error -> {
            // Show error state
            val fallbackLayout = """
            {
              "column": {
                "modifier": {
                  "base": {
                    "fillMaxWidth": true,
                    "padding": {
                      "all": 16
                    }
                  },
                  "verticalArrangement": "center",
                  "horizontalAlignment": "center"
                },
                "children": [
                  {
                    "text": {
                      "content": "Could not load layout from database",
                      "textAlign": "center"
                    }
                  }
                ]
              }
            }
            """
            val component = createLayoutComponent(fallbackLayout)
            DynamicLayout(component = component)
        }
    }
}

// Example repository implementation
class LayoutRepository(
    private val layoutDao: LayoutDao,
    private val apiClient: LayoutApiClient
) {
    suspend fun getLayout(layoutKey: String): Result<String> {
        // Try to get from local database first
        val localLayout = layoutDao.getLayoutByKey(layoutKey)

        return if (localLayout != null) {
            // Return cached layout
            Result.Success(localLayout.jsonContent)
        } else {
            try {
                // Fetch from API if not in database
                val remoteLayout = apiClient.fetchLayout(layoutKey)

                // Cache the fetched layout
                layoutDao.insertLayout(LayoutEntity(layoutKey, remoteLayout))

                Result.Success(remoteLayout)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}

// Result wrapper class
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
```

## Advanced Implementation Patterns

### Layout Versioning

Include version information to handle compatibility:

```kotlin
// Define your layout response structure
data class LayoutResponse(
    val version: String,
    val minAppVersion: String,
    val layout: String
)

// Check compatibility before rendering
@Composable
fun VersionedLayout(layoutResponse: LayoutResponse) {
    val appVersion = BuildConfig.VERSION_NAME

    if (isVersionCompatible(appVersion, layoutResponse.minAppVersion)) {
        // Version is compatible, render the layout
        val component = createLayoutComponent(layoutResponse.layout)
        DynamicLayout(component = component)
    } else {
        // Version incompatible, show update message
        UpdateRequiredScreen()
    }
}

// Version comparison utility
fun isVersionCompatible(appVersion: String, minRequiredVersion: String): Boolean {
    // Implement semantic version comparison logic
    // Return true if appVersion >= minRequiredVersion
    return true // Simplified for this example
}
```

### Caching Strategy

Implement a smart caching strategy for better performance and offline support:

```kotlin
class LayoutCache(private val context: Context) {
    private val preferences = context.getSharedPreferences(
        "layout_cache", Context.MODE_PRIVATE
    )

    fun saveLayout(key: String, json: String, timestamp: Long = System.currentTimeMillis()) {
        preferences.edit()
            .putString("${key}_json", json)
            .putLong("${key}_timestamp", timestamp)
            .apply()
    }

    fun getLayout(key: String): CachedLayout? {
        val json = preferences.getString("${key}_json", null) ?: return null
        val timestamp = preferences.getLong("${key}_timestamp", 0)
        return CachedLayout(json, timestamp)
    }

    fun isExpired(key: String, maxAgeMs: Long): Boolean {
        val timestamp = preferences.getLong("${key}_timestamp", 0)
        return System.currentTimeMillis() - timestamp > maxAgeMs
    }
}

data class CachedLayout(val json: String, val timestamp: Long)

// Usage in a Composable
@Composable
fun CachedRemoteLayout(
    layoutKey: String,
    apiClient: LayoutApiClient,
    layoutCache: LayoutCache,
    maxAgeMs: Long = 24 * 60 * 60 * 1000 // 24 hours
) {
    var layoutJson by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(layoutKey) {
        // Try to get from cache first
        val cachedLayout = layoutCache.getLayout(layoutKey)

        if (cachedLayout != null && !layoutCache.isExpired(layoutKey, maxAgeMs)) {
            // Use cached layout if not expired
            layoutJson = cachedLayout.json
            isLoading = false

            // Fetch updated layout in background
            launch {
                try {
                    val freshLayout = apiClient.fetchLayout(layoutKey)
                    if (freshLayout != cachedLayout.json) {
                        // Update cache and UI if there's a new layout
                        layoutCache.saveLayout(layoutKey, freshLayout)
                        layoutJson = freshLayout
                    }
                } catch (e: Exception) {
                    // Continue using cached layout on error
                }
            }
        } else {
            // No valid cache, must fetch from network
            try {
                val freshLayout = apiClient.fetchLayout(layoutKey)
                layoutCache.saveLayout(layoutKey, freshLayout)
                layoutJson = freshLayout
                isLoading = false
            } catch (e: Exception) {
                // Try to use expired cache as last resort
                if (cachedLayout != null) {
                    layoutJson = cachedLayout.json
                } else {
                    layoutJson = """{"text": {"content": "Could not load layout"}}"""
                }
                isLoading = false
            }
        }
    }

    if (isLoading) {
        // Show loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Create and render component
        val component = createLayoutComponent(layoutJson)
        DynamicLayout(component = component)
    }
}
```

## Implementation Tips

### 1. Design for Network Failures

Always assume network requests can fail:

```kotlin
try {
    // Attempt to fetch layout
    layoutJson = apiClient.fetchLayout(layoutKey)
} catch (NetworkException e) {
    // Use progressively more basic fallbacks
    layoutJson = try {
        layoutCache.getLayout(layoutKey)?.json
    } catch (CacheException ce) {
        try {
            context.assets.open("fallbacks/$layoutKey.json").bufferedReader().use { it.readText() }
        } catch (AssetException ae) {
            """{"text": {"content": "Unable to load content. Please try again."}}"""
        }
    }
}
```

### 2. Implement Analytics for Layout Performance

Track layout loading and rendering performance:

```kotlin
val startTime = System.currentTimeMillis()

// Parse layout
val component = createLayoutComponent(layoutJson)
val parseTime = System.currentTimeMillis() - startTime

// Log metrics
analytics.trackEvent("layout_loaded") {
    param("layout_key", layoutKey)
    param("layout_size_bytes", layoutJson.length)
    param("parse_time_ms", parseTime)
}

// Set up a LayoutInfo receiver to track render times
val renderInfo = remember { LayoutRenderInfo() }

DynamicLayout(
    component = component,
    onRenderComplete = { renderTime ->
        analytics.trackEvent("layout_rendered") {
            param("layout_key", layoutKey)
            param("render_time_ms", renderTime)
        }
    }
)
```

### 3. Use Feature Flags with Remote Layouts

Combine feature flags with your layouts for controlled rollouts:

```kotlin
// Get feature flags
val featureFlags = featureFlagsService.getFlags()

// Select appropriate layout based on flags
val layoutKey = when {
    featureFlags.isEnabled("new_home_ui") -> "home_new"
    featureFlags.isInBetaGroup() -> "home_beta"
    else -> "home_standard"
}

// Fetch and render the selected layout
val layoutJson = apiClient.fetchLayout(layoutKey)
val component = createLayoutComponent(layoutJson)
DynamicLayout(component = component)
```

## Next Steps

Now that you understand how to integrate remote layouts, you can:

1. Learn about [binding values](../05-bind-values) to add dynamic content
2. Implement [action handling](../06-bind-actions) for user interactions
3. Check out the sample apps in the repository for complete implementations