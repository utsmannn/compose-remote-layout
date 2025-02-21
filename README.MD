# Compose Remote Layout

## 1. Background and Motivation

In modern app development, the ability to update UI components without deploying new app versions is
increasingly important. While solutions like React Native and Flutter offer this capability, they
often require learning new frameworks or languages. Compose Remote Layout bridges this gap for
Kotlin Multiplatform applications by enabling dynamic UI updates while staying within the Compose
ecosystem.

This library was created to solve several key challenges:

- The need for rapid UI iterations without app store submissions
- A/B testing requirements for UI components
- Platform-specific UI customization from a central source
- Real-time UI updates for critical changes

## 2. Key Features

The compose-remote-layout module provides several core features:

1. **Component System**
    - Built-in support for basic Compose components (Column, Row, Box, Text, Button, Card)
    - All components can be defined and modified through JSON
    - Component properties map directly to Compose parameters

2. **Dynamic Updates**
    - Load layouts from remote sources (API, Firebase, local files)
    - Update UI without app redeployment
    - Handle layout changes in real-time

3. **Value Binding**
    - Dynamic text updates using the BindsValue system
    - Template-based value substitution
    - Real-time value changes support

4. **Modifier System**
    - Comprehensive modifier support matching Compose capabilities
    - Scoped modifiers for specific component types
    - JSON-based modifier definition

5. **Custom Components**
    - Register custom Composable functions
    - Map JSON definitions to custom UI elements
    - Pass custom parameters and handle specific logic

## 3. Download

Add the dependencies to your `build.gradle.kts`:

```kotlin
// Kotlin Multiplatform
implementation("io.github.utsmannn:compose-remote-layout:0.0.1-alpha01")

// Choose platform-specific implementations as needed:
implementation("io.github.utsmannn:compose-remote-layout-android:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-jvm:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-iosx64:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-iosarm64:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-js:0.0.1-alpha01")
```

## 4. Usage Guide

### 4.1 Default Usage

Basic implementation using built-in components:

```kotlin
@Composable
fun MyScreen(layoutJson: String) {
    val layoutComponent = remember(layoutJson) {
        LayoutParser.parseLayoutJson(layoutJson)
    }

    DynamicLayout(
        component = layoutComponent,
        onClickHandler = { clickId ->
            // Handle click events
        }
    )
}
```

Example JSON layout:

```json
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
          "content": "Hello World"
        }
      }
    ]
  }
}
```

### 4.2 Custom Node Registration

Create and register custom components:

```kotlin
// Register during app initialization
CustomNodes.register("banner") { param ->
    Column(
        modifier = param.modifier
    ) {
        Text(
            text = param.data["title"] ?: "unknown",
            style = MaterialTheme.typography.h2
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = param.data["message"] ?: "unknown"
        )
    }
}
```

Use in JSON:

```json
{
  "banner": {
    "data": {
      "title": "Welcome",
      "message": "This is a custom banner"
    },
    "modifier": {
      "base": {
        "padding": {
          "all": 16
        }
      }
    }
  }
}
```

### 4.3 Modifier System

The library supports extensive modifiers through JSON:

```json
"modifier": {
   "base": {
   "width": 200,
   "height": 100,
   "padding": {
      "all": 16
   },
   "background": {
      "color": "#FF0000",
      "shape": "roundedcorner",
      "radius": 8
      }
   }
}
```

### 4.4 Scoped Modifiers

Different components support specific modifiers:

```json
// Column-specific modifiers
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true
      },
      "verticalArrangement": "spaceBetween",
      "horizontalAlignment": "center"
    }
  }
}

// Row-specific modifiers
{
  "row": {
    "modifier": {
      "horizontalArrangement": "spaceBetween",
      "verticalAlignment": "center"
    }
  }
}
```

## 5. How It Works

The library uses several key architectural approaches to enable dynamic UI rendering:

### 5.1 Component System

The component system is built on a sealed class hierarchy:

```kotlin
sealed class LayoutComponent {
    abstract val modifier: LayoutModifier?

    data class Column(...)
    data class Row(...)
    data class Box(...)
    data class Text(...)
    data class Button(...)
    data class Card(...)
    data class Custom(...)
}
```

Components are wrapped in a `ComponentWrapper` class that helps with type safety and component
identification:

```kotlin
data class ComponentWrapper(
    val column: LayoutComponent.Column? = null,
    val row: LayoutComponent.Row? = null,
    // other components...
)
```

### 5.2 JSON Parsing Architecture

The JSON parsing process follows these steps:

1. **Initial Parsing**: Uses kotlinx.serialization to parse JSON string
2. **Modifier Order Extraction**: Analyzes JSON structure to maintain consistent modifier ordering
3. **Component Type Detection**: Identifies component types and creates appropriate wrappers
4. **Custom Component Handling**: Checks for registered custom components
5. **Validation**: Ensures required fields are present

Key parsing code:

```kotlin
object LayoutParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = false
    }

    fun parseLayoutJson(jsonString: String): LayoutComponent? {
        val jsonElement = json.parseToJsonElement(jsonString)
        val modifierOrder = extractModifierOrder(jsonElement)
        ModifierOrderTracker.setCurrentOrder(modifierOrder)
        return parseComponentWrapper(jsonElement).component
    }
}
```

### 5.3 Modifier System

The modifier system uses a hierarchical approach:

1. **Base Modifiers**: Universal properties applicable to all components
   ```kotlin
   data class BaseModifier(
       val width: Int?,
       val height: Int?,
       val padding: PaddingValues?,
       // other universal properties...
   )
   ```

2. **Scoped Modifiers**: Component-specific properties
   ```kotlin
   sealed class ScopedModifier {
       abstract val base: BaseModifier
       
       data class Column(
           override val base: BaseModifier,
           val verticalArrangement: String?,
           val horizontalAlignment: String?
       ) : ScopedModifier()
       // other scoped modifiers...
   }
   ```

3. **Modifier Application**: Modifiers are applied in a specific order tracked by
   `ModifierOrderTracker`

### 5.4 Dynamic Layout Rendering

The rendering process involves several key components:

1. **DynamicLayout Composable**:
   ```kotlin
   @Composable
   fun DynamicLayout(
       component: LayoutComponent?,
       modifier: Modifier = Modifier,
       path: String = "root",
       parentScrollable: Boolean = false,
       bindValue: BindsValue = BindsValue(),
       onClickHandler: (String) -> Unit = {}
   )
   ```

2. **Component Caching**: Uses `DynamicLayoutRenderer` to cache and reuse components
   ```kotlin
   object DynamicLayoutRenderer {
       private val componentCache = mutableMapOf<String, LayoutComponent>()
       
       fun saveComponent(path: String, component: LayoutComponent)
       fun getLastValidComponent(path: String): LayoutComponent?
       fun clearCache()
   }
   ```

3. **Path-based Rendering**: Uses string paths to track component hierarchy
   ```text
   root-column-0-button-1
   ```

### 5.5 Value Binding System

The value binding system enables dynamic updates:

1. **State Management**:
   ```kotlin
   class BindsValue {
       private val _textStates: MutableStateFlow<Map<String, Any>>
       val textStates: StateFlow<Map<String, Any>>
   }
   ```

2. **Template Processing**:
   ```kotlin
   companion object {
       fun get(component: LayoutComponent.Text, states: Map<String, Any>): String? {
           if (component.content.startsWith("{") && component.content.endsWith("}")) {
               val key = component.content.replace("{", "").replace("}", "")
               return states[key]?.toString()
           }
           return component.content
       }
   }
   ```

### 5.6 Custom Component Integration

Custom components are managed through a registry system:

1. **Registration**:
   ```kotlin
   object CustomNodes {
       private val nodes = mutableMapOf<String, @Composable (NodeParam) -> Unit>()
       
       fun register(type: String, node: @Composable (NodeParam) -> Unit)
   }
   ```

2. **Parameter Handling**:
   ```kotlin
   data class NodeParam(
       val data: Map<String, String>,
       val modifier: Modifier,
       val children: List<ComponentWrapper>?,
       val path: String
   )
   ```

### 5.7 Event Handling

The library handles events through:

1. **Click Handlers**: Passed through DynamicLayout
2. **Scrolling State**: Managed per-component with scrollable flag
3. **Parent-Child Communication**: Through path-based component tracking

All these systems work together to provide a flexible and maintainable dynamic UI system while
maintaining type safety and performance.

## 6. Sample Projects

### Firebase Remote Config Integration

The repository includes a [Firebase sample project](samples/firebaseApp) demonstrating:

1. **Remote Config Setup**: Integration with Firebase Remote Config
2. **Real-time Updates**: Layout updates without app deployment
3. **Cross-platform Support**: Implementation for Android and iOS
4. **Error Handling**: Fallback mechanisms and error states

## 7. JSON Builder Guide

### 7.1 Using jsonBuilderWeb Module

The [jsonBuilderWeb](jsonBuilderWeb) module provides a web interface for:

- Creating and editing layouts
- Real-time preview of changes
- Schema validation
- Remote config integration

To run the JSON builder:

```shell
./gradlew :jsonBuilderWeb:jsBrowserRun
```

### 7.2 Firebase Remote Config Integration

The [`remote-config-server`](jsonBuilderWeb/remote-config-server) in the jsonBuilderWeb module provides:

- Local development server for testing
- Firebase Remote Config simulation
- API endpoints for layout updates
- Configuration management interface

To run the remote config server:

```shell
npm run start
```

## 8. Potential Use Cases

This library can handle various scenarios:

1. **A/B Testing**
    - Test different layouts with user segments
    - Gather metrics on UI performance
    - Quick iteration on designs

2. **Dynamic Content**
    - Seasonal UI changes
    - Feature announcements
    - Promotional content

3. **Platform Customization**
    - Platform-specific layouts
    - Device-specific optimizations
    - Regional variations

4. **Rapid Iterations**
    - Quick fix for UI issues
    - New feature rollouts
    - Experimental features

## 9. Important Disclaimer

⚠️ **Early Development Stage**

This library is currently in alpha stage (version 0.0.1-alpha01) and has several important
considerations:

1. **Production Use**: Not recommended for production applications at this time
2. **API Stability**: The API may change significantly between versions
3. **Performance**: Not fully optimized for large-scale applications
4. **Testing**: Limited real-world testing and validation
5. **Documentation**: May be incomplete or subject to changes

We recommend:

- Using this library for experimental projects
- Contributing feedback and bug reports
- Waiting for stable releases before production use
- Testing thoroughly in your specific use case

Please report issues and suggestions through the GitHub repository.