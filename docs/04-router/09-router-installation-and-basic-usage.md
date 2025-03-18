This guide covers adding the Compose Remote Layout Router to your project and setting up the basic
requirements.

## Adding Dependencies

Add the router dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    // Core library (required)
    implementation("io.github.utsmannn:compose-remote-layout:$version")

    // Router module
    implementation("io.github.utsmannn:compose-remote-layout-router:$version")

    // Platform-specific implementations
    implementation("io.github.utsmannn:compose-remote-layout-android:$version") // For Android
    // OR
    implementation("io.github.utsmannn:compose-remote-layout-jvm:$version") // For Desktop
    // OR
    implementation("io.github.utsmannn:compose-remote-layout-js:$version") // For Web
    // OR iOS implementations
    implementation("io.github.utsmannn:compose-remote-layout-iosx64:$version")
    implementation("io.github.utsmannn:compose-remote-layout-iosarm64:$version")
}
```

Replace `$version` with the latest version number. You can find the current version
on [Maven Central](https://central.sonatype.com/artifact/io.github.utsmannn/compose-remote-layout-router).

## Basic Setup Requirements

To use the router, you'll need:

1. A **CoroutineScope** for asynchronous operations
2. A **LayoutFetcher** to retrieve layouts
3. A **RemoteRouter** instance to manage navigation

## Minimal Implementation

Here's a minimal example to get started:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.router.ComposeRemoteRouter
import com.utsman.composeremote.router.KtorHttpLayoutFetcher
import com.utsman.composeremote.router.RenderEvent
import com.utsman.composeremote.router.ResultRouterFactory
import com.utsman.composeremote.router.cached

@Composable
fun RouterBasicExample() {
    // Create a coroutine scope
    val scope = rememberCoroutineScope()

    // Create a layout fetcher with caching
    val fetcher = remember {
        val ktorFetcher = KtorHttpLayoutFetcher()
        ktorFetcher.cached()
    }

    // Create the router
    val router = remember {
        ResultRouterFactory().createRouter(
            scope = scope,
            fetcher = fetcher,
            baseUrl = "https://your-api.com/layouts" // Your base URL for layouts
        )
    }

    // Implement the router UI
    ComposeRemoteRouter(
        initialPath = "/home", // First screen to load
        router = router
    ) { renderEvent ->
        when (renderEvent) {
            is RenderEvent.Loading -> {
                // Show loading UI
                LoadingIndicator()
            }
            is RenderEvent.Failure -> {
                // Show error UI
                ErrorScreen(renderEvent.error)
            }
            is RenderEvent.RenderedLayout -> {
                // Render the layout
                DynamicLayout(
                    component = renderEvent.component,
                    bindValue = renderEvent.bindsValue,
                    onClickHandler = renderEvent.clickEvent
                )
            }
        }
    }
}

// Simple loading indicator composable
@Composable
fun LoadingIndicator() {
    // Your loading UI implementation
}

// Simple error screen composable
@Composable
fun ErrorScreen(error: Throwable) {
    // Your error UI implementation
}
```

## Back Button Handling

To handle the system back button, add a back handler:

```kotlin
val isRoot by router.isRoot.collectAsState()

BackHandler(enabled = !isRoot) {
    router.popPath()
}
```