## Introduction

Compose Remote Layout Router is a powerful navigation system designed specifically for dynamic,
server-driven UIs in Compose applications. It enables developers to create flexible, updateable
applications where both the UI components and navigation flow can be controlled remotely from a
server without requiring app updates.

Server-driven UI (SDUI) decouples the UI definition from the client application by delivering layout
information from a server. Compose Remote Layout Router extends this concept to navigation, allowing
complete user journeys to be defined and updated remotely.

## Key Features

### Navigation Stack Management

The router maintains a comprehensive history stack of screens, enabling:

- Forward navigation to new screens
- Backward navigation to previously visited screens
- Deep linking to specific screens
- Complete control over the navigation history

### Remote Layout Fetching

- Automatically retrieves layout JSON from remote endpoints when navigating to new screens
- Handles network requests and error states gracefully
- Supports various data formats optimized for UI rendering
- Integrates with Ktor for efficient network communication

### Transition Animations

- Provides smooth, customizable transitions between screens
- Supports multiple animation types (fade, slide, etc.)
- Allows different transition types for different navigation actions (push, pop, replace)
- Configurable animation durations and easing functions

### Caching System

- Built-in intelligent caching for layouts to improve performance
- Configurable cache size and time-to-live (TTL) settings
- Support for offline usage through cached layouts
- Memory-efficient implementation with LRU (Least Recently Used) eviction policy

### Click-Based Navigation

- Enables navigation directly from JSON layouts using special clickId prefixes
- Supports common navigation patterns: push, pop, replace, home, and reload
- Simplifies navigation implementation in server-defined layouts
- Consistent navigation behavior across the entire application

## Core Components

The router system consists of several key components that work together:

### RemoteRouter

An interface that manages navigation state and handles navigation actions:

```kotlin
interface RemoteRouter {
    val baseUrl: String
    val urlStack: MutableList<String>
    val currentUrl: StateFlow<String>
    val previousUrl: StateFlow<String?>
    // Additional properties

    fun pushPath(path: String)
    fun popPath(): Boolean
    fun replacePath(path: String)
    fun clearHistory()
    fun reload()
}
```

The RemoteRouter:

- Maintains the URL stack for navigation history
- Provides methods for standard navigation actions (push, pop, replace)
- Exposes state flows for the current and previous URLs
- Tracks navigation status (isRoot, etc.)

### ComposeRemoteRouter

A Composable function that integrates with the UI and renders the current screen:

```kotlin
@Composable
fun ComposeRemoteRouter(
    initialPath: String,
    router: RemoteRouter,
    // Additional parameters
    onRenderEvent: @Composable (RenderEvent) -> Unit,
)
```

The ComposeRemoteRouter:

- Connects RemoteRouter state to the UI
- Handles screen transitions with animations
- Provides callbacks for navigation events
- Renders the appropriate UI based on loading/success/error states

### LayoutFetcher

An interface for retrieving layout JSON from various sources:

```kotlin
interface LayoutFetcher {
    suspend fun fetchLayout(url: String): Result<String>
    fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>>
}
```

The LayoutFetcher:

- Defines methods for synchronous and asynchronous layout retrieval
- Supports different backend implementations (HTTP, local, etc.)
- Handles loading states and errors consistently
- Can be extended with caching capabilities

### NavigationEventContainer

A container for navigation events that can be triggered from anywhere in the app:

```kotlin
class NavigationEventContainer {
    val event: StateFlow<NavigationEvent?>

    fun push(path: String)
    fun replace(path: String)
    fun home(path: String)
    fun pop()
    fun reload()
}
```

The NavigationEventContainer:

- Provides methods to trigger navigation (push, pop, replace, etc.)
- Centralizes navigation event handling
- Simplifies navigation from non-Composable contexts
- Integrates seamlessly with ComposeRemoteRouter

### RenderEvent

Represents different states of the rendering process:

```kotlin
sealed class RenderEvent {
    data class RenderedLayout(/*...*/) : RenderEvent()
    data class Loading(val path: String) : RenderEvent()
    data class Failure(val error: Throwable, val path: String) : RenderEvent()
}
```

The RenderEvent system:

- Loading: When a layout is being fetched
- Success: When a layout has been successfully fetched and parsed
- Failure: When an error occurs during fetching or parsing

## When to Use Router

The Compose Remote Layout Router is especially useful for:

- **Apps requiring frequent UI updates** without deploying new versions
    - Marketing campaigns, seasonal themes, promotions
    - Feature rollouts and A/B testing
    - Content-driven applications

- **A/B testing different screen flows** without app store submissions
    - Test multiple user journeys simultaneously
    - Quickly iterate on conversion funnels
    - Measure performance of different navigation patterns

- **Personalized user journeys** based on user preferences or behavior
    - Customized onboarding flows
    - User-specific features and screens
    - Context-aware navigation

- **Complex navigation patterns** that need to be modified remotely
    - Multi-step forms
    - Checkout processes
    - Configuration wizards

- **Multi-step flows** like onboarding, checkout processes, or forms
    - Control the exact sequence of screens
    - Add/remove steps based on server logic
    - Update guidance and help screens remotely

## Architecture Benefits

Using the router offers several architectural advantages:

1. **Separation of Concerns**:
    - Navigation logic is decoupled from UI rendering
    - Clean architecture with clear responsibilities
    - Easier testing and maintenance

2. **Consistent Navigation Pattern**:
    - Standard approach for all screen transitions
    - Predictable user experience
    - Simplified navigation implementation

3. **Server-Driven Navigation**:
    - Navigation flow can be updated without app changes
    - Coordinated UI and navigation updates
    - Centralized control over user journeys

4. **Performance Optimization**:
    - Built-in caching and prefetching capabilities
    - Reduced network requests through intelligent caching
    - Optimized memory usage

5. **Better User Experience**:
    - Smooth transitions between screens
    - Offline support through caching
    - Faster loading times for previously visited screens

## Integration with Compose Remote Layout

Compose Remote Layout Router seamlessly integrates with the core Compose Remote Layout library,
allowing:

- Dynamic layouts to be loaded and displayed
- Binding values to be passed to layouts
- Click events to be handled both for UI interaction and navigation
- Complete server-driven experiences from simple screens to complex flows

## Getting Started

To start using the Compose Remote Layout Router, continue with
the [Installation and Basic Usage](../09-router-installation-and-basic-usage.md) guide.