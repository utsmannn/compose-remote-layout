The Bind Value system in Compose Remote Layout enables you to create dynamic UI components that can
update in response to changes in your application state, without requiring new JSON layouts from
your server.

## Overview

The `BindsValue` class serves as a container for key-value pairs that can be referenced in your JSON
layout. When these values change, components that reference them automatically update. This is
particularly useful for:

- Displaying real-time data
- Updating UI based on user interactions
- Implementing counters, timers, and other dynamic elements
- Creating form inputs that reflect state changes

## Basic Usage

### 1. Create a BindsValue instance

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun DynamicValueExample() {
    // Create a BindsValue instance
    val bindsValue = remember { BindsValue() }

    // Set initial values
    bindsValue.setValue("username", "John Doe")
    bindsValue.setValue("points", 150)

    // Your JSON layout
    val layoutJson = """
    {
      "column": {
        "children": [
          {
            "text": {
              "content": "Welcome, {username}!"
            }
          },
          {
            "text": {
              "content": "You have {points} points"
            }
          }
        ]
      }
    }
    """

    // Create and render the component with bound values
    val component = createLayoutComponent(layoutJson)
    DynamicLayout(
        component = component,
        bindValue = bindsValue
    )
}
```

### 2. Reference bound values in JSON

You can reference bound values in your JSON layout using curly braces syntax: `{key}`. For example:

```json
{
  "text": {
    "content": "Hello, {username}!"
  }
}
```

When the DynamicLayout renders this component, it will replace `{username}` with the actual value
bound to the "username" key.

### 3. Update values dynamically

You can update bound values at any time, and components will automatically reflect these changes:

```kotlin
@Composable
fun CounterExample() {
    val bindsValue = remember { BindsValue() }
    var counter by remember { mutableStateOf(0) }

    // Update the bound value whenever counter changes
    LaunchedEffect(counter) {
        bindsValue.setValue("counter", counter)
    }

    Column {
        // Display the counter value in a dynamic layout
        DynamicLayout(
            component = createLayoutComponent(
                """
                {
                  "text": {
                    "content": "Count: {counter}",
                    "fontSize": 24
                  }
                }
                """
            ),
            bindValue = bindsValue
        )

        // Button to increment the counter
        Button(onClick = { counter++ }) {
            Text("Increment")
        }
    }
}
```

## Advanced Usage

### Binding to Custom Components

You can bind values to custom components through the `data` property:

```json
{
  "custom_chart": {
    "title": "Sales Overview",
    "data_source": "{chart_data}",
    "show_legend": "{show_legend}"
  }
}
```

In your custom component implementation:

```kotlin
CustomNodes.register("custom_chart") { param ->
    val title = param.data["title"] ?: "Chart"
    val dataSource = param.data["data_source"]
    val showLegend = param.data["show_legend"]?.toBoolean() ?: true

    // Use these values to render your custom chart
    // ...
}
```

### Combining Multiple BindsValue Instances

You can combine multiple `BindsValue` instances using the `+` operator:

```kotlin
val userBinds = remember { BindsValue() }
userBinds.setValue("username", "John")
userBinds.setValue("role", "Admin")

val statsBinds = remember { BindsValue() }
statsBinds.setValue("visits", 245)
statsBinds.setValue("conversion", "15%")

// Combine both instances
val combinedBinds = userBinds + statsBinds

// Now you can use all keys: {username}, {role}, {visits}, {conversion}
DynamicLayout(
    component = createLayoutComponent(layoutJson),
    bindValue = combinedBinds
)
```

### Using Composition Local

The library provides a `LocalBindsValue` CompositionLocal that allows child components to access
bound values without explicit passing:

```kotlin
@Composable
fun ParentComponent() {
    val bindsValue = remember { BindsValue() }
    bindsValue.setValue("theme", "dark")

    CompositionLocalProvider(LocalBindsValue provides bindsValue) {
        // All child components can now access the bindsValue
        ChildComponent()
    }
}

@Composable
fun ChildComponent() {
    // Access the parent's bindsValue through CompositionLocal
    val parentBinds = LocalBindsValue.current

    // Create a new bindsValue and combine with parent's
    val localBinds = remember { BindsValue() }
    localBinds.setValue("component", "child")

    val combinedBinds = localBinds + parentBinds

    DynamicLayout(
        component = createLayoutComponent(layoutJson),
        bindValue = combinedBinds
    )
}
```

## Practical Examples

### User Profile Card

```kotlin
@Composable
fun UserProfileCard(user: User) {
    val bindsValue = remember { BindsValue() }

    // Update binds when user changes
    LaunchedEffect(user) {
        bindsValue.setValue("name", user.name)
        bindsValue.setValue("email", user.email)
        bindsValue.setValue("role", user.role)
        bindsValue.setValue("joinDate", user.formatJoinDate())
        bindsValue.setValue("avatarUrl", user.avatarUrl)
    }

    DynamicLayout(
        component = createLayoutComponent(profileCardJson),
        bindValue = bindsValue
    )
}
```

### Real-time Data Dashboard

```kotlin
@Composable
fun DataDashboard(viewModel: DashboardViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val bindsValue = remember { BindsValue() }

    // Update binds when metrics change
    LaunchedEffect(metrics) {
        bindsValue.setValue("activeUsers", metrics.activeUsers.toString())
        bindsValue.setValue("conversion", "${metrics.conversionRate}%")
        bindsValue.setValue("revenue", "$${metrics.revenue}")
        bindsValue.setValue("trend", if (metrics.isPositiveTrend) "▲" else "▼")
        bindsValue.setValue("trendColor", if (metrics.isPositiveTrend) "#00AA00" else "#FF0000")
    }

    DynamicLayout(
        component = createLayoutComponent(dashboardJson),
        bindValue = bindsValue
    )
}
```

## Best Practices

1. **Use meaningful keys**: Choose descriptive key names that clearly indicate their purpose.

2. **Separate concerns**: Group related values into their own BindsValue instances, then combine
   them when needed.

3. **React to changes efficiently**: Use `LaunchedEffect` to update bound values only when the
   source data changes.

4. **Provide fallbacks**: In your JSON layouts, consider default values for cases where bound values
   might be missing.

5. **Cache when appropriate**: For expensive operations, compute the value once and bind it rather
   than recomputing each time.

6. **Monitor performance**: Be mindful of binding very large or frequently changing values that
   might impact rendering performance.

## Next Steps

Now that you understand how to bind dynamic values to your layouts, learn
about [handling click events](../basic-usage/click-event) to create interactive UIs.