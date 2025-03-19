The Compose Remote Layout library comes with built-in components like Column, Row, Text, and Button.
However, your application often requires specialized UI elements that aren't part of the standard
set. Custom Nodes allow you to extend the library with your own reusable components.

## Understanding Custom Nodes

Custom Nodes are a way to register your own Composable functions that can be referenced in JSON
layouts. This creates a powerful extension mechanism that allows you to:

1. **Create application-specific components** that match your design system
2. **Encapsulate complex UI logic** in reusable components
3. **Integrate third-party libraries** with Compose Remote Layout
4. **Maintain consistency** across your application

## How Custom Nodes Work

The `CustomNodes` object is a registry for all your custom components. It maintains a map of
component type names to Composable functions:

```kotlin
object CustomNodes {
    private val nodes = mutableMapOf<String, @Composable (NodeParam) -> Unit>()

    fun register(
        type: String,
        node: @Composable (NodeParam) -> Unit,
    ) {
        nodes[type.lowercase()] = node
    }

    fun get(type: String): (@Composable (NodeParam) -> Unit)? = nodes[type.lowercase()]

    fun exists(type: String): Boolean = nodes.containsKey(type.lowercase())

    fun clear() {
        nodes.clear()
    }
}
```

When the DynamicLayout encounters a component type that doesn't match any built-in component, it
checks if a custom node is registered with that type. If found, it calls the corresponding
Composable function.

## Registering Custom Nodes

To register a custom node, call the `CustomNodes.register()` function, typically during application
initialization:

```kotlin
// In your Application class or composition root
fun registerCustomComponents() {
    // Register a custom profile card component
    CustomNodes.register("profile_card") { param ->
        // Implementation of your custom component
        Card(
            modifier = param.modifier,
            elevation = 4.dp
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = param.data["initials"] ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // User info
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = param.data["name"] ?: "Unknown",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = param.data["role"] ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // Register other custom components...
}
```

## The NodeParam Object

Your custom component implementation receives a `NodeParam` object containing all the information
needed to render the component:

```kotlin
data class NodeParam(
    val data: Map<String, String>,          // Properties from JSON
    val modifier: Modifier,                 // Combined modifiers
    val children: List<ComponentWrapper>?,  // Child components (if any)
    val path: String,                       // Component path in the tree
    val parentScrollable: Boolean,          // If parent is scrollable
    val onClickHandler: (String) -> Unit,   // Click event handler
    val bindsValue: BindsValue              // Value bindings
)
```

Let's explore each parameter:

### Data

The `data` map contains all properties defined in the JSON for your custom component:

```json
{
  "profile_card": {
    "name": "John Doe",
    "role": "Software Engineer",
    "initials": "JD",
    "level": "Senior"
  }
}
```

Access these properties in your implementation:

```kotlin
val name = param.data["name"] ?: "Unknown"
val role = param.data["role"] ?: ""
val initials = param.data["initials"] ?: "?"
val level = param.data["level"] ?: ""
```

Always provide defaults for optional properties to make your components robust.

### Modifier

The `modifier` parameter contains all modifiers applied to your component, including base modifiers
like width, height, padding, etc.

```kotlin
// Use the provided modifier directly
Card(
    modifier = param.modifier,
    elevation = 4.dp
) {
    // Component content
}

// Or combine with additional modifiers
Box(
    modifier = param.modifier
        .padding(8.dp)  // Add extra padding
        .clip(RoundedCornerShape(8.dp)),  // Add corner clipping
    contentAlignment = Alignment.Center
) {
    // Component content
}
```

### Children

The `children` parameter contains any child components defined in the JSON:

```json
{
  "expandable_section": {
    "title": "Section Title",
    "expanded": "true",
    "children": [
      {
        "text": {
          "content": "Child content 1"
        }
      },
      {
        "text": {
          "content": "Child content 2"
        }
      }
    ]
  }
}
```

Render these children in your implementation:

```kotlin
CustomNodes.register("expandable_section") { param ->
    val title = param.data["title"] ?: "Section"
    val isExpanded = param.data["expanded"]?.toBoolean() ?: false

    var expanded by remember { mutableStateOf(isExpanded) }

    Column(modifier = param.modifier) {
        // Header with toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        // Content (children)
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                // Render each child
                param.children?.forEach { childWrapper ->
                    DynamicLayout(
                        component = childWrapper.component,
                        path = "${param.path}-child-${childWrapper.hashCode()}",
                        parentScrollable = param.parentScrollable,
                        onClickHandler = param.onClickHandler,
                        bindValue = param.bindsValue
                    )
                }
            }
        }
    }
}
```

### Path

The `path` parameter provides the component's position in the layout tree, which is useful for
maintaining state and managing children:

```kotlin
// Use the path for uniqueness in keys or IDs
val stateKey = "${param.path}-expanded"
var expanded by remember(stateKey) { mutableStateOf(isExpanded) }

// Pass path to child components
DynamicLayout(
    component = childComponent,
    path = "${param.path}-child-$index",
    // Other parameters...
)
```

### ParentScrollable

The `parentScrollable` flag indicates if the parent component is scrollable, which helps avoid
nested scrolling issues:

```kotlin
// Only make this component scrollable if the parent isn't
val scrollModifier = if (!param.parentScrollable && needsScrolling) {
    Modifier.verticalScroll(rememberScrollState())
} else {
    Modifier
}

Column(
    modifier = param.modifier.then(scrollModifier)
) {
    // Component content
}
```

### OnClickHandler

The `onClickHandler` function allows your custom component to trigger click events that are handled
by the parent:

```kotlin
CustomNodes.register("action_button") { param ->
    val actionId = param.data["action_id"] ?: "default_action"
    val label = param.data["label"] ?: "Action"

    Button(
        onClick = {
            // Forward the action to the parent handler
            param.onClickHandler("custom_action:$actionId")
        },
        modifier = param.modifier
    ) {
        Text(label)
    }
}
```

### BindsValue

The `bindsValue` parameter provides access to the current bound values, allowing your component to
access dynamic data:

```kotlin
CustomNodes.register("user_greeting") { param ->
    // Access a value from bindings
    val username = param.bindsValue.getValue<String>(
        LayoutComponent.Custom(
            type = "user_greeting",
            data = param.data,
            modifier = null,
            children = null
        ),
        "username"
    ) ?: "Guest"

    Text(
        text = "Welcome, $username!",
        style = MaterialTheme.typography.h5,
        modifier = param.modifier
    )
}
```

## Using Custom Nodes in JSON

Once registered, you can use your custom nodes in JSON layouts:

```json
{
  "column": {
    "children": [
      {
        "profile_card": {
          "name": "John Doe",
          "role": "Software Engineer",
          "initials": "JD",
          "level": "Senior",
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "padding": {
                "all": 8
              },
              "clickId": "view_profile:john.doe"
            }
          }
        }
      },
      {
        "expandable_section": {
          "title": "Recent Activity",
          "expanded": "true",
          "children": [
            {
              "text": {
                "content": "Updated profile information"
              }
            },
            {
              "text": {
                "content": "Completed project milestone"
              }
            }
          ]
        }
      }
    ]
  }
}
```

## Real-World Examples

Let's explore some practical custom components you might create:

### Rating Component

```kotlin
CustomNodes.register("star_rating") { param ->
    val rating = param.data["rating"]?.toFloatOrNull() ?: 0f
    val maxRating = param.data["max"]?.toIntOrNull() ?: 5
    val size = param.data["size"]?.toIntOrNull() ?: 24
    val color = param.data["color"]?.let { ColorParser.parseColor(it) } ?: Color.Gold

    Row(modifier = param.modifier) {
        for (i in 1..maxRating) {
            val filled = i <= rating
            val halfFilled = !filled && i - 0.5f <= rating

            Icon(
                imageVector = when {
                    filled -> Icons.Filled.Star
                    halfFilled -> Icons.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}
```

Usage:

```json
{
  "star_rating": {
    "rating": "4.5",
    "max": "5",
    "size": "32",
    "color": "#FFD700"
  }
}
```

### Input Field

```kotlin
CustomNodes.register("input_field") { param ->
    val field = remember {
        TextFieldState(
            initialValue = param.data["value"] ?: "",
            label = param.data["label"] ?: "",
            placeholder = param.data["placeholder"] ?: "",
            isPassword = param.data["password"]?.toBoolean() ?: false,
            onChange = { newValue ->
                // Report changes to parent
                val fieldId = param.data["id"] ?: "field"
                param.onClickHandler("field_change:$fieldId:$newValue")
            }
        )
    }

    // Get any error message from bindings
    val errorKey = param.data["error_key"] ?: "${param.data["id"]}_error"
    val error = param.bindsValue.getValue<String>(
        LayoutComponent.Custom(
            type = "input_field",
            data = param.data,
            modifier = null,
            children = null
        ),
        errorKey
    ) ?: ""

    Column(modifier = param.modifier) {
        if (field.label.isNotEmpty()) {
            Text(
                text = field.label,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        OutlinedTextField(
            value = field.value,
            onValueChange = {
                field.value = it
                field.onChange(it)
            },
            placeholder = {
                if (field.placeholder.isNotEmpty()) {
                    Text(field.placeholder)
                }
            },
            visualTransformation = if (field.isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            isError = error.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// Helper class for field state
private class TextFieldState(
    initialValue: String,
    val label: String,
    val placeholder: String,
    val isPassword: Boolean,
    val onChange: (String) -> Unit
) {
    var value by mutableStateOf(initialValue)
}
```

Usage:

```json
{
  "input_field": {
    "id": "email",
    "label": "Email Address",
    "placeholder": "Enter your email",
    "value": "{email_value}",
    "error_key": "email_error"
  }
}
```

### Chart Component

```kotlin
CustomNodes.register("chart") { param ->
    val chartType = param.data["type"] ?: "bar"
    val title = param.data["title"] ?: ""
    val dataKey = param.data["data_key"] ?: "chart_data"
    val height = param.data["height"]?.toIntOrNull() ?: 200

    // Get data from bindings
    val chartData = param.bindsValue.getValue<String>(
        LayoutComponent.Custom(
            type = "chart",
            data = param.data,
            modifier = null,
            children = null
        ),
        dataKey
    ) ?: "[]"

    // Parse data
    val data = try {
        Json.decodeFromString<List<ChartDataPoint>>(chartData)
    } catch (e: Exception) {
        emptyList()
    }

    Column(modifier = param.modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .border(1.dp, Color.LightGray)
        ) {
            when (chartType.lowercase()) {
                "bar" -> BarChart(data)
                "line" -> LineChart(data)
                "pie" -> PieChart(data)
                else -> {
                    Text(
                        text = "Unsupported chart type: $chartType",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

// Chart implementation components
@Composable
private fun BarChart(data: List<ChartDataPoint>) {
    // Your bar chart implementation
}

@Composable
private fun LineChart(data: List<ChartDataPoint>) {
    // Your line chart implementation
}

@Composable
private fun PieChart(data: List<ChartDataPoint>) {
    // Your pie chart implementation
}

// Data class for chart data
@Serializable
private data class ChartDataPoint(
    val label: String,
    val value: Float,
    val color: String? = null
)
```

Usage:

```json
{
  "chart": {
    "type": "bar",
    "title": "Monthly Sales",
    "data_key": "sales_data",
    "height": "300"
  }
}
```

## Best Practices

### 1. Use Descriptive Type Names

Choose clear, descriptive names for your custom components:

```kotlin
// Good
CustomNodes.register("profile_card") { /* ... */ }
CustomNodes.register("input_field") { /* ... */ }
CustomNodes.register("product_carousel") { /* ... */ }

// Avoid
CustomNodes.register("card1") { /* ... */ }
CustomNodes.register("custom_input") { /* ... */ }
CustomNodes.register("carousel") { /* ... */ }
```

### 2. Always Provide Defaults

Make your components robust by providing defaults for all properties:

```kotlin
val title = param.data["title"] ?: "Untitled"
val count = param.data["count"]?.toIntOrNull() ?: 0
val isEnabled = param.data["enabled"]?.toBoolean() ?: true
val color = param.data["color"]?.let { ColorParser.parseColor(it) } ?: Color.Black
```

### 3. Handle Type Conversions Safely

Always use safe conversions for non-string types:

```kotlin
// Safe integer conversion
val size = param.data["size"]?.toIntOrNull() ?: 16

// Safe boolean conversion
val isVisible = param.data["visible"]?.toBoolean() ?: true

// Safe float conversion
val opacity = param.data["opacity"]?.toFloatOrNull() ?: 1.0f

// Safe color parsing
val color = param.data["color"]?.let {
    try {
        ColorParser.parseColor(it)
    } catch (e: Exception) {
        Color.Black // Fallback color
    }
} ?: Color.Black
```

### 4. Document Your Components

Document your custom components for other developers:

```kotlin
/**
 * Rating Component
 *
 * Displays a star rating with customizable properties.
 *
 * Properties:
 * - rating: The rating value (0-5, supports half stars)
 * - max: Maximum number of stars (default: 5)
 * - size: Size of each star in dp (default: 24)
 * - color: Star color in hex format (default: gold)
 *
 * Example:
 * ```json
 * {
 *   "star_rating": {
 *     "rating": "4.5",
 *     "max": "5",
 *     "size": "32",
 *     "color": "#FFD700"
 *   }
 * }
 * ```

*/
CustomNodes.register("star_rating") { param ->
// Implementation...
}

```

### 5. Keep Components Focused

Each custom component should have a single responsibility:

```kotlin
// Good: Focused component
CustomNodes.register("price_display") { param ->
    val price = param.data["price"]?.toDoubleOrNull() ?: 0.0
    val currency = param.data["currency"] ?: "$"
    
    // Render price with currency
}

// Avoid: Component doing too much
CustomNodes.register("product_card") { param ->
    // Handles image, title, price, description, rating, actions, etc.
    // Too many responsibilities in one component
}
```

### 6. Reuse Built-in Components

Build on top of built-in components when possible:

```kotlin
CustomNodes.register("section_header") { param ->
    val title = param.data["title"] ?: ""
    val subtitle = param.data["subtitle"]

    Column(modifier = param.modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
```

### 7. Register Early in App Lifecycle

Register all your custom components during app initialization:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register all custom components
        registerCustomComponents()
    }

    private fun registerCustomComponents() {
        // UI components
        CustomNodes.register("profile_card") { /* ... */ }
        CustomNodes.register("section_header") { /* ... */ }

        // Form components
        CustomNodes.register("input_field") { /* ... */ }
        CustomNodes.register("dropdown") { /* ... */ }

        // Visualization components
        CustomNodes.register("chart") { /* ... */ }
        CustomNodes.register("progress_indicator") { /* ... */ }

        // Special components
        CustomNodes.register("map_view") { /* ... */ }
        CustomNodes.register("image_gallery") { /* ... */ }
    }
}
```

## Troubleshooting

### Component Not Appearing

If your custom component doesn't appear:

1. Verify the component type is registered correctly (check case sensitivity)
2. Ensure the registration happens before the layout is rendered
3. Check for exceptions in your component implementation
4. Verify the JSON structure matches what your component expects

### Data Not Being Passed Correctly

If properties aren't being passed correctly:

1. Check the JSON property names match what your code is looking for
2. Ensure you're handling type conversions safely
3. Verify your defaults are working as expected

### Children Not Rendering

If child components aren't rendering:

1. Check that you're correctly passing the children to DynamicLayout
2. Ensure you're using the correct path for child components
3. Verify the parent-child relationship in your JSON

## Next Steps

Now that you understand custom nodes, you can:

1. Design a component system for your application
2. Create reusable UI elements that match your design language
3. Integrate third-party libraries with Compose Remote Layout