This guide introduces the fundamentals of using Compose Remote Layout in your application. The
library works by converting JSON strings into dynamic UI components.

## Core Concept

At its heart, Compose Remote Layout takes a JSON string that defines your UI layout, converts it
into Compose components, and renders them. This JSON string can come from anywhere:

- Hardcoded in your app (for testing or fallback layouts)
- Loaded from local assets
- Retrieved from a remote API
- Fetched from a configuration service like Firebase Remote Config
- Generated dynamically based on user preferences

## Creating Your First Dynamic Layout

Let's start with a basic layout that displays a text element:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun SimpleLayout() {
    // Step 1: Define your layout as a JSON string
    // This JSON describes a column with a text element inside
    val jsonLayout = """
    {
      "column": {                       // Root component is a Column
        "modifier": {                   // Apply modifiers to the Column
          "base": {
            "fillMaxWidth": true,       // Make it fill the available width
            "padding": {
              "all": 16                 // Add 16dp padding on all sides
            }
          }
        },
        "children": [                   // Child components of the Column
          {
            "text": {                   // A Text component
              "content": "Hello from Remote Layout!",  // The text to display
              "fontSize": 20,           // 20sp font size
              "fontWeight": "bold",     // Bold font weight
              "color": "#0066CC"        // Text color in hex format
            }
          }
        ]
      }
    }
    """

    // Step 2: Convert the JSON string to a LayoutComponent object
    val component = createLayoutComponent(jsonLayout)

    // Step 3: Render the component using DynamicLayout
    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            // We'll handle clicks in a later section
        }
    )
}
```

## Understanding the Flow

1. **JSON Definition**: You define your UI structure in JSON format. This could be stored anywhere.

2. **Parsing**: The `createLayoutComponent()` function parses the JSON string and converts it to a
   structured `LayoutComponent` object that Compose can understand.

3. **Rendering**: The `DynamicLayout` composable takes the parsed component and renders it using
   native Compose components.

## Default Components

Compose Remote Layout supports these built-in components:

- `column`: Vertical arrangement of children (equivalent to `Column` in Compose)
- `row`: Horizontal arrangement of children (equivalent to `Row` in Compose)
- `box`: Overlay arrangement of children (equivalent to `Box` in Compose)
- `grid`: Grid arrangement of children
- `text`: Text display
- `button`: Clickable button
- `card`: Material card component
- `spacer`: Empty space

## Using null or invalid JSON

If you provide null or invalid JSON, the library will use a default component:

```kotlin
// This will render a default component
DynamicLayout(component = null)

// This will also render a default component if the parsing fails
val invalidJsonComponent = createLayoutComponent("not a valid json")
DynamicLayout(component = invalidJsonComponent)
```

The default component is a simple Column with a Button and a Text element.

## Next Steps

Now that you understand the basic setup, you can:

1. Learn about [integration with remote sources](../../02-setup/03-integrated-remote-sources) to
   fetch layouts dynamically
2. Explore the [JSON structure](../../02-setup/04-layout-json-structure) in detail
3. Add dynamic content with [bind values](../../02-setup/05-bind-values)
4. Make your UI interactive with [click actions](../../02-setup/06-bind-actions)