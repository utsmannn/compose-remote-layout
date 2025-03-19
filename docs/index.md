# Compose Remote Layout

**Server-Driven UI Component for Compose Multiplatform**

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.utsmannn/compose-remote-layout)](https://central.sonatype.com/artifact/io.github.utsmannn/compose-remote-layout)
[![Publish to Maven Central](https://github.com/utsmannn/compose-remote-layout/actions/workflows/publish.yaml/badge.svg)](https://github.com/utsmannn/compose-remote-layout/actions/workflows/publish.yaml)

## Overview

Compose Remote Layout empowers you to dynamically update your UI without app store submissions.
Built for Compose Multiplatform, this library transforms JSON into native UI components, giving you
the flexibility to modify interfaces on the fly.

By enabling server-driven UI within the Compose ecosystem, you can:

- Update your app's look and feel instantly
- A/B test different layouts with different user segments
- Fix UI issues without emergency releases
- Deliver platform-specific experiences from a central source

## Why Use Compose Remote Layout?

While solutions like React Native and Flutter offer dynamic UI updates, they require learning new
frameworks or languages. Compose Remote Layout brings these capabilities to Compose developers:

- **No New Languages to Learn** - Stay within the Compose ecosystem
- **Native Performance** - Uses standard Compose components under the hood
- **Multiplatform Support** - Works with Compose Multiplatform for Android, iOS, Desktop, and Web
- **Granular Control** - Update specific screens or components, not the entire app
- **Lightweight** - Minimal impact on app size and performance

## Key Features

### Component System

The library provides built-in support for all essential Compose components:

```kotlin
// JSON definition
val json = """
{
  "column": {
    "children": [
      { "text": { "content": "Hello World!" } },
      { "button": { "content": "Click Me", "clickId": "my_button" } }
    ]
  }
}
"""

// Simple rendering
DynamicLayout(component = createLayoutComponent(json))
```

- **Core Components** - Column, Row, Box, Text, Button, Card, Spacer, and Grid
- **Modifiers** - Complete modifier support matching native Compose capabilities
- **Nested Components** - Create complex layouts with unlimited nesting

### Dynamic Updates

Load layouts from various sources to update your UI without redeployment:

```kotlin
// From Firebase Remote Config
remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val layoutJson = remoteConfig.getString("home_screen")
        val component = createLayoutComponent(layoutJson)
        DynamicLayout(component = component)
    }
}
```

- **Multiple Sources** - API responses, Firebase Remote Config, local files, databases
- **Fallback Support** - Graceful degradation when remote sources are unavailable
- **Caching** - Store layouts for offline use and faster loading

### Value Binding

Connect your layouts to dynamic data with the BindsValue system:

```kotlin
// Create and populate bindings
val bindsValue = remember { BindsValue() }
bindsValue.setValue("username", user.displayName)
bindsValue.setValue("itemCount", cart.items.size.toString())

// Reference in JSON with {key} syntax
val json = """
{
  "text": {
    "content": "Welcome, {username}! You have {itemCount} items in your cart."
  }
}
"""
```

- **Real-time Updates** - Values update automatically when underlying data changes
- **Type Support** - Bind to text content, colors, font sizes, and other properties
- **Composable Integration** - Works seamlessly with Compose state management

### Action Binding

Create interactive UIs with click event handling:

```kotlin
DynamicLayout(
    component = component,
    onClickHandler = { clickId ->
        when (clickId) {
            "login_button" -> viewModel.login()
            "signup_button" -> navController.navigate("signup")
            "settings" -> openSettings()
        }
    }
)
```

- **Event Handling** - Connect clicks to your application logic
- **Parameterized Actions** - Pass data through structured click IDs
- **Architecture Integration** - Works with ViewModel, MVI, and other patterns

### Custom Components

Extend the library with your own components:

```kotlin
// Register a custom component
CustomNodes.register("profile_card") { param ->
    val name = param.data["name"] ?: "Unknown"
    val avatarUrl = param.data["avatar_url"]

    Card(modifier = param.modifier) {
        // Your custom implementation
    }
}

// Use in JSON
val json = """
{
  "profile_card": {
    "name": "John Doe",
    "avatar_url": "https://example.com/avatar.jpg"
  }
}
"""
```

- **Custom UI Elements** - Create reusable components that fit your app's needs
- **Data Passing** - Send arbitrary data to custom components
- **Composition Support** - Custom components can contain other components

### Cross-Platform

Develop once, deploy everywhere:

- **Android** - Native support through Jetpack Compose
- **iOS** - Support through Compose Multiplatform or standalone Swift package
- **Desktop** - Works with Compose for Desktop
- **Web** - Compatible with Compose for Web

## Use Cases

### A/B Testing

Deploy multiple layout variations to different user segments:

```kotlin
val layoutKey = when {
    user.isInTestGroup("new-home-ui") -> "home_new"
    user.isInBetaProgram() -> "home_beta"
    else -> "home_standard"
}

val layoutJson = remoteConfig.getString(layoutKey)
```

### Dynamic Content

Update UI for seasonal changes, promotions, or feature announcements without app updates:

```kotlin
val layoutJson = when {
    isHolidaySeason() -> remoteConfig.getString("home_holiday")
    isPromoActive() -> remoteConfig.getString("home_promo")
    hasNewFeature() -> remoteConfig.getString("home_new_feature")
    else -> remoteConfig.getString("home_default")
}
```

### Platform Customization

Deliver optimized experiences for different devices:

```kotlin
val layoutKey = when {
    isTablet() -> "product_detail_tablet"
    isLandscape() -> "product_detail_landscape"
    else -> "product_detail_phone"
}
```

### Rapid Iterations

Quickly fix UI issues or test new designs without app store submissions:

```kotlin
// Fetch the latest layout version
val layoutJson = api.fetchLayout("checkout_screen", buildConfig.VERSION_CODE)

// Apply with fallback for errors
try {
    val component = createLayoutComponent(layoutJson)
    DynamicLayout(component = component)
} catch (e: Exception) {
    // Fall back to bundled layout
    val bundledJson = loadJsonFromAssets("checkout_fallback.json")
    val fallbackComponent = createLayoutComponent(bundledJson)
    DynamicLayout(component = fallbackComponent)
}
```

## Getting Started

Get started with Compose Remote Layout:

- [Installation](./01-getting-started/01-installation.md) - Add the library to your project
- [Basic Setup](./01-getting-started/02-basic-setup.md) - Create your first dynamic layout

## Setup

Learn more about the library's core features:

- [Remote Sources](./02-setup/03-integrated-remote-sources.md) - Integrate with APIs and config
  services
- [JSON Structure](03-json-structure/06-layout-json-structure) - Learn the layout format
- [Bind Values](./02-setup/04-bind-values) - Create dynamic content
- [Bind Actions](./02-setup/05-bind-actions) - Handle user interactions

## Sample Projects

Explore complete examples in the repository:

- *
  *[Firebase Integration](https://github.com/utsmannn/compose-remote-layout/tree/master/samples/firebaseApp)
  ** - Complete implementation with Firebase Remote Config
- *
  *[Custom Components](https://github.com/utsmannn/compose-remote-layout/tree/master/samples/customComponents)
  ** - Examples of extending the library
- *
  *[Form Builder](https://github.com/utsmannn/compose-remote-layout/tree/master/samples/formBuilder)
  ** - Dynamic form creation and validation

## JSON Builder Tool

The repository includes a web-based JSON builder tool for creating and testing layouts:

![JSON Builder Tool](https://github.com/utsmannn/compose-remote-layout/raw/master/images/json_builder.png)

To use the JSON builder:

```shell
./gradlew :jsonBuilderWeb:jsBrowserRun
```

## Current Status

⚠️ **Early Development Stage**

Compose Remote Layout is currently in alpha stage. While functional for many use cases, please note:

- API may change between versions
- Test thoroughly before production use
- Not all Compose features are supported yet
- Performance optimizations are ongoing

We recommend:

- Using this library for experimental projects
- Contributing feedback and bug reports
- Waiting for stable releases before critical production use

## Get Involved

- [GitHub Repository](https://github.com/utsmannn/compose-remote-layout)
- [Report Issues](https://github.com/utsmannn/compose-remote-layout/issues)
- [Medium Article](https://medium.com/@utsmannn/server-driven-ui-with-compose-remote-layout-bdc902d973f8)