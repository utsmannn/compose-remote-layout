The Compose Remote Layout Router uses a URL-based routing system to navigate between different
layouts. This guide explains how routes work and the patterns for effective navigation.

## URL Structure

The router combines a base URL with paths to create the full URL for fetching layouts:

Full URL = baseUrl + path

Examples:

```
- "https://api.example.com/layouts" + "/home" = "https://api.example.com/layouts/home"
- "https://api.example.com/layouts" + "/product/123" = "https://api.example.com/layouts/product/123"
```

```kotlin
val router = ResultRouterFactory().createRouter(
    scope = coroutineScope,
    fetcher = layoutFetcher,
    baseUrl = "https://api.example.com/layouts"
)
```

## Navigation Actions

The router provides several navigation methods:

### Push Navigation

Adds a new screen to the navigation stack:

```kotlin
// Navigate to products screen
router.pushPath("/products")

// Navigate to specific product
router.pushPath("/product/123")

// Navigate with query parameters
router.pushPath("/search?query=shoes&sort=price")
```

### Pop Navigation (Back)

Returns to the previous screen:

```kotlin
// Go back to previous screen
val success = router.popPath()

// The method returns true if successful, false if at the root
if (!success) {
    // At root screen, handle accordingly
}
```

### Replace Navigation

Replaces the current screen without adding to the stack:

```kotlin
// Replace current screen with settings
router.replacePath("/settings")
```

### Home Navigation

Clears the history and navigates to a specific path:

```kotlin
// Clear history and go to home
router.clearHistory()
router.pushPath("/home")
```

### Refresh Current Screen

Reloads the current screen:

```kotlin
// Reload current layout
router.reload()
```

## Navigation from JSON

One of the most powerful features is the ability to define navigation directly in your JSON layouts
using special `clickId` prefixes:

### Push Navigation

```json
{
  "button": {
    "content": "Go to Products",
    "clickId": "navigate:/products"
  }
}
```

### Replace Navigation

```json
{
  "button": {
    "content": "Go to Settings",
    "clickId": "replace:/settings"
  }
}
```

### Back Navigation

```json
{
  "button": {
    "content": "Back",
    "clickId": "back"
  }
}
```

### Home Navigation

```json
{
  "button": {
    "content": "Home",
    "clickId": "home"
  }
}
```

### Refresh Navigation

```json
{
  "button": {
    "content": "Refresh",
    "clickId": "reload"
  }
}
```

## Navigation with Parameters

You can include parameters in your navigation paths:

### Path Parameters

```kotlin
// In Kotlin
router.pushPath("/product/123")

// In JSON
{
    "button": {
       "content": "View Product",
       "clickId": "navigate:/product/123"
   }
}
```

### Query Parameters

```kotlin
// In Kotlin
router.pushPath("/search?query=shoes&sort=price")

// In JSON
{
    "button": {
       "content": "Search Shoes",
       "clickId": "navigate:/search?query=shoes&sort=price"
   }
}
```

## Programmatic Navigation with NavigationEventContainer

For more control, you can use the `NavigationEventContainer`:

```kotlin
// Create the container
val navigationEventContainer = remember { NavigationEventContainer() }

// Setup the router with the container
ComposeRemoteRouter(
    initialPath = "/home",
    router = router,
    navigationEventContainer = navigationEventContainer
) { renderEvent ->
    // Render content
}

// Trigger navigation from anywhere
Button(onClick = { navigationEventContainer.push("/products") }) {
    Text("Go to Products")
}

Button(onClick = { navigationEventContainer.pop() }) {
    Text("Back")
}

Button(onClick = { navigationEventContainer.replace("/settings") }) {
    Text("Settings")
}

Button(onClick = { navigationEventContainer.home("/home") }) {
    Text("Home")
}

Button(onClick = { navigationEventContainer.reload() }) {
    Text("Reload")
}
```

## Handling Navigation Events

You can listen for navigation events using the `onNavigateHandler`:

```kotlin
ComposeRemoteRouter(
    initialPath = "/home",
    router = router,
    onNavigateHandler = { event ->
        when (event) {
            is NavigationEvent.Push -> {
                println("Navigated to: ${event.path}")
                analytics.logScreenView(event.path)
            }
            is NavigationEvent.Pop -> {
                println("Navigated back")
                analytics.logBack()
            }
            is NavigationEvent.Replace -> {
                println("Replaced with: ${event.path}")
                analytics.logScreenView(event.path)
            }
            is NavigationEvent.Home -> {
                println("Went home to: ${event.path}")
                analytics.logScreenView(event.path)
            }
            is NavigationEvent.Reload -> {
                println("Reloaded screen")
                analytics.logReload()
            }
        }
    }
) { renderEvent ->
    // Render content
}
```

## Handling Back Button

To properly handle the system back button:

```kotlin
// Track if at root of navigation stack
val isRoot by router.isRoot.collectAsState()

// Handle back button
BackHandler(enabled = !isRoot) {
    router.popPath()
}
```

## Recommended Route Patterns

For consistent navigation, consider these route patterns:

### 1. Section/ID Pattern

```
/section/[id]

Examples:
/products
/product/123
/categories
/category/electronics
```

### 2. Action Pattern

```
/section/[id]/[action]

Examples:
/product/123/reviews
/product/123/related
/user/profile/edit
```

### 3. Query Parameters for Filters

```
/section?[param]=[value]

Examples:
/products?category=electronics&sort=price
/search?query=shoes&color=black
```

## Best Practices

1. **Use Consistent Patterns**
   Establish a clear pattern for your routes and stick to it

2. **Keep Routes Simple**
   Avoid overly complex nested routes

3. **Handle Edge Cases**
   Always handle navigation failures gracefully

4. **Use Meaningful Names**
   Choose descriptive route names that reflect the content

5. **Track Navigation Analytics**
   Log navigation events for understanding user flows