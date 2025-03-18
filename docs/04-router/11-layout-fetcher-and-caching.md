The Compose Remote Layout Router uses the `LayoutFetcher` interface to retrieve layout JSON from
various sources. This component is crucial for performance and offline functionality.

## LayoutFetcher Interface

All layout fetchers implement this interface:

```kotlin
interface LayoutFetcher {
    suspend fun fetchLayout(url: String): Result<String>
    fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>>
}
```

This interface defines two methods:

- `fetchLayout`: Simple suspend function that returns a Result
- `fetchLayoutAsFlow`: Returns a Flow with loading states, useful for UI feedback

## Built-in Fetchers

### KtorHttpLayoutFetcher

The primary fetcher uses [Ktor HTTP Client](https://ktor.io/docs/getting-started-ktor-client.html) to
retrieve layouts from remote endpoints:

```kotlin
// Create with default settings
val fetcher = KtorHttpLayoutFetcher()

// Create with custom Ktor client
val client = HttpClient {
    install(Logging) {
        level = LogLevel.HEADERS
    }
    install(HttpCache)  // Built-in HTTP caching
    install(HttpTimeout) {
        requestTimeoutMillis = 10000  // 10 seconds
    }
}
val customFetcher = KtorHttpLayoutFetcher(client)
```

The default implementation includes:

- HTTP request logging
- Basic HTTP caching
- Standard error handling

### CachedKtorLayoutFetcher

This wrapper adds in-memory caching capabilities to any `LayoutFetcher`:

```kotlin
// Create with default settings
val cachedFetcher = ktorFetcher.cached()

// Create with custom settings
val customCachedFetcher = ktorFetcher.cached(
    maxCacheSize = 100,                               // Cache up to 100 layouts
    cacheTtlMillis = 15.minutes.inWholeMilliseconds  // Cache for 15 minutes
)
```

## Caching Configuration

### Cache Size

Control how many layouts are stored in memory:

```kotlin
// Small cache for memory-constrained devices
val smallCache = fetcher.cached(maxCacheSize = 20)

// Large cache for complex apps with many screens
val largeCache = fetcher.cached(maxCacheSize = 200)
```

### Cache TTL (Time-To-Live)

Control how long layouts are considered valid:

```kotlin
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.days

// Short TTL for frequently changing content
val shortTtlCache = fetcher.cached(cacheTtlMillis = 5.minutes.inWholeMilliseconds)

// Medium TTL for normal content
val mediumTtlCache = fetcher.cached(cacheTtlMillis = 1.hours.inWholeMilliseconds)

// Long TTL for static content
val longTtlCache = fetcher.cached(cacheTtlMillis = 1.days.inWholeMilliseconds)
```

## Cache Management

The cache can be manually managed:

```kotlin
// Clear the entire cache
cachedFetcher.clearCache()

// Remove a specific URL from cache
cachedFetcher.invalidateCache("https://api.example.com/layouts/home")
```

Use cases for manual cache management:

- When a user logs out (clear all)
- After submitting a form that changes data (invalidate specific URLs)
- After app updates (clear all)
- When forcing a refresh (invalidate specific URL)

## Cache Implementation Details

The `CachedKtorLayoutFetcher` provides:

1. **Thread-safe operations** using Mutex
2. **LRU eviction policy** (Least Recently Used)
3. **TTL-based expiration** (Time To Live)
4. **Transparent operation** - falls back to network when cache misses

The cache flow works as follows:

1. Check if URL exists in cache and is not expired
2. If found and valid, return cached layout
3. If not found or expired, fetch from network
4. If network fetch succeeds, store in cache
5. Return the layout

## Combined HTTP and Memory Caching

For optimal performance, use both HTTP caching and memory caching:

```kotlin
// Create HTTP client with caching
val client = HttpClient {
    install(HttpCache)  // HTTP-level caching
}

// Create fetcher with HTTP cache
val httpCachedFetcher = KtorHttpLayoutFetcher(client)

// Add memory caching layer
val fullyCachedFetcher = httpCachedFetcher.cached()
```

This provides multiple caching layers:

1. HTTP Cache: Handles conditional requests, ETags, etc.
2. Memory Cache: Provides fastest access without network

## Sample Usage Scenarios

### Basic Network with Caching

```kotlin
val router = ResultRouterFactory().createRouter(
    scope = coroutineScope,
    fetcher = KtorHttpLayoutFetcher().cached(),
    baseUrl = "https://api.example.com/layouts"
)
```

### Different Cache Settings for Different Content

```kotlin
// Create different caches for different content types
val staticFetcher = KtorHttpLayoutFetcher().cached(
    cacheTtlMillis = 24.hours.inWholeMilliseconds
)

val dynamicFetcher = KtorHttpLayoutFetcher().cached(
    cacheTtlMillis = 5.minutes.inWholeMilliseconds
)

// Select fetcher based on URL
val compositeFetcher = object : LayoutFetcher {
    override suspend fun fetchLayout(url: String): Result<String> {
        return if (url.contains("/static/")) {
            staticFetcher.fetchLayout(url)
        } else {
            dynamicFetcher.fetchLayout(url)
        }
    }

    override fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>> {
        return if (url.contains("/static/")) {
            staticFetcher.fetchLayoutAsFlow(url)
        } else {
            dynamicFetcher.fetchLayoutAsFlow(url)
        }
    }
}

val router = ResultRouterFactory().createRouter(
    scope = coroutineScope,
    fetcher = compositeFetcher,
    baseUrl = "https://api.example.com/layouts"
)
```

### Cache Refresh on User Action

```kotlin
@Composable
fun ProductScreen(productId: String, fetcher: CachedKtorLayoutFetcher) {
    val router = rememberRemoteRouter()

    // Function to refresh product data
    val refreshProduct = {
        // Invalidate all related product caches
        fetcher.invalidateCache("https://api.example.com/layouts/product/$productId")
        fetcher.invalidateCache("https://api.example.com/layouts/product/$productId/reviews")

        // Reload current screen
        router.reload()
    }

    // Pull-to-refresh implementation
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = { refreshProduct() }
    ) {
        ComposeRemoteRouter(
            initialPath = "/product/$productId",
            router = router
        ) { renderEvent ->
            // Render content
        }
    }

    // Additional refresh button
    FloatingActionButton(onClick = { refreshProduct() }) {
        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
    }
}
```

## Best Practices

### 1. Always Use Caching

```kotlin
// Don't do this in production
val fetcher = KtorHttpLayoutFetcher()  // No caching

// Do this instead
val cachedFetcher = KtorHttpLayoutFetcher().cached()
```

### 2. Tailor Cache TTL to Content Type

```kotlin
// Static content (rarely changes)
val staticFetcher = KtorHttpLayoutFetcher().cached(
    cacheTtlMillis = 24.hours.inWholeMilliseconds
)

// Dynamic content (changes frequently)
val dynamicFetcher = KtorHttpLayoutFetcher().cached(
    cacheTtlMillis = 5.minutes.inWholeMilliseconds
)
```

### 3. Handle Network Errors Gracefully

```kotlin
ComposeRemoteRouter(
    initialPath = "/home",
    router = router,
) { renderEvent ->
    when (renderEvent) {
        is RenderEvent.Loading -> {
            LoadingIndicator()
        }
        is RenderEvent.Failure -> {
            if (renderEvent.error is IOException) {
                // Network error - show offline message with retry button
                OfflineMessage(
                    onRetry = { router.reload() }
                )
            } else {
                // Other error
                ErrorMessage(renderEvent.error.message ?: "Unknown error")
            }
        }
        is RenderEvent.RenderedLayout -> {
            DynamicLayout(
                component = renderEvent.component,
                bindValue = renderEvent.bindsValue,
                onClickHandler = renderEvent.clickEvent
            )
        }
    }
}
```

### 4. Clear Cache When Appropriate

```kotlin
// When user logs out
fun logout() {
    // Clear all user data
    userPreferences.clear()

    // Clear layout cache to avoid showing personalized content
    cachedFetcher.clearCache()

    // Navigate to login
    router.clearHistory()
    router.pushPath("/login")
}
```

### 5. Monitor Cache Performance

```kotlin
// Add cache hit/miss monitoring
var cacheHits = 0
var cacheMisses = 0

val monitoredFetcher = object : LayoutFetcher {
    override suspend fun fetchLayout(url: String): Result<String> {
        val startTime = System.currentTimeMillis()
        val result = cachedFetcher.fetchLayout(url)
        val duration = System.currentTimeMillis() - startTime

        // Guess if it was a cache hit based on response time
        if (duration < 50) {
            cacheHits++
        } else {
            cacheMisses++
        }

        // Log some stats
        println("Cache hit rate: ${cacheHits.toFloat() / (cacheHits + cacheMisses)}")

        return result
    }

    override fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>> {
        return cachedFetcher.fetchLayoutAsFlow(url)
    }
}
```