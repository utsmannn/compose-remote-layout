The Bind Value system is a powerful feature of Compose Remote Layout that enables dynamic content
updates without changing your JSON layout definition. This guide explains how to use bind values to
create reactive UIs.

## Understanding Bind Values

Bind Values allow you to:

1. **Update text and properties dynamically** - Change content based on state, user input, or data
2. **Create reactive interfaces** - Reflect changes immediately when underlying data changes
3. **Personalize experiences** - Show user-specific content without different layout files
4. **Connect remote layouts to local data** - Bridge your JSON layouts with app data

## How Bind Values Work

1. You create a `BindsValue` instance in your Kotlin code
2. You set key-value pairs in this instance using `setValue()`
3. In your JSON layout, you reference these keys using the `{key}` syntax
4. When the layout renders, `{key}` is replaced with the current value

## Basic Usage

### Step 1: Create a BindsValue Instance

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.createLayoutComponent

@Composable
fun DynamicWelcomeScreen(username: String) {
    // Create a BindsValue instance
    val bindsValue = remember { BindsValue() }

    // Set values to be used in the layout
    bindsValue.setValue("username", username)
    bindsValue.setValue("appName", "My Awesome App")

    val layoutJson = """
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
              "content": "Welcome to {appName}!",
              "fontSize": 24,
              "fontWeight": "bold"
            }
          },
          {
            "text": {
              "content": "Hello, {username}",
              "fontSize": 18
            }
          }
        ]
      }
    }
    """

    // Pass the BindsValue to DynamicLayout
    val component = createLayoutComponent(layoutJson)
    DynamicLayout(
        component = component,
        bindValue = bindsValue
    )
}
```

### Step 2: Reference Values in Your JSON

Use the `{key}` syntax to reference bound values in your JSON:

```json
{
  "text": {
    "content": "Welcome back, {username}!",
    "color": "{primaryColor}"
  }
}
```

### Step 3: Update Values Dynamically

Values can be updated at any time, and the UI will reflect the changes:

```kotlin
@Composable
fun CounterExample() {
    // State for the counter
    var counter by remember { mutableStateOf(0) }

    // Create and remember a BindsValue instance
    val bindsValue = remember { BindsValue() }

    // Update bindsValue whenever counter changes
    LaunchedEffect(counter) {
        bindsValue.setValue("count", counter.toString())

        // You can also set computed values
        val textColor = when {
            counter > 10 -> "#00AA00"  // Green for high values
            counter < 0 -> "#AA0000"   // Red for negative values
            else -> "#000000"          // Black for normal values
        }
        bindsValue.setValue("countColor", textColor)
    }

    Column {
        // Display the counter using DynamicLayout
        DynamicLayout(
            component = createLayoutComponent(
                """
                {
                  "text": {
                    "content": "Count: {count}",
                    "fontSize": 24,
                    "color": "{countColor}"
                  }
                }
            """
            ),
            bindValue = bindsValue
        )

        // Regular Compose UI for buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { counter-- }) {
                Text("-")
            }
            Button(onClick = { counter++ }) {
                Text("+")
            }
        }
    }
}
```

## Advanced Usage

### Binding to Different Property Types

You can bind values to various component properties, not just text content:

```kotlin
// Set different types of values
bindsValue.setValue("isVisible", true)
bindsValue.setValue("buttonColor", "#0066CC")
bindsValue.setValue("fontSize", 16)

// JSON with bindings to different properties
val json = """
{
  "column": {
    "children": [
      {
        "text": {
          "content": "This text has {fontSize}sp font size",
          "fontSize": {fontSize},
          "color": "{textColor}"
        }
      },
      {
        "button": {
          "content": "Click Me",
          "fontColor": "{buttonColor}"
        }
      }
    ]
  }
}
"""
```

### Binding with Custom Components

Bind values work with custom components through the `data` property:

```kotlin
// Register a custom chart component
CustomNodes.register("analytics_chart") { param ->
    val chartData = param.data["dataset"] ?: "[]"
    val showLegend = param.data["showLegend"]?.toBoolean() ?: true

    // Parse the data and render chart
    val dataset = parseChartData(chartData)

    Chart(
        dataset = dataset,
        showLegend = showLegend,
        modifier = param.modifier
    )
}

// In your composable
val bindsValue = remember { BindsValue() }

// Update chart data
bindsValue.setValue("chartData", "[10, 24, 15, 32, 18, 27]")
bindsValue.setValue("showLegendOption", "true")

// JSON with custom component
val json = """
{
  "analytics_chart": {
    "dataset": "{chartData}",
    "showLegend": "{showLegendOption}"
  }
}
"""

DynamicLayout(
    component = createLayoutComponent(json),
    bindValue = bindsValue
)
```

### Combining Multiple BindsValue Instances

You can combine different BindsValue instances using the `+` operator:

```kotlin
@Composable
fun ProfileScreen(user: User, theme: AppTheme) {
    // Create separate BindsValue instances for different concerns
    val userBinds = remember { BindsValue() }
    val themeBinds = remember { BindsValue() }

    // Update user-related values
    LaunchedEffect(user) {
        userBinds.setValue("username", user.displayName)
        userBinds.setValue("email", user.email)
        userBinds.setValue("memberSince", user.joinDate.format("MMM yyyy"))
    }

    // Update theme-related values
    LaunchedEffect(theme) {
        themeBinds.setValue("primaryColor", theme.primaryColor)
        themeBinds.setValue("textColor", theme.textColor)
        themeBinds.setValue("backgroundColor", theme.backgroundColor)
    }

    // Combine the BindsValue instances
    val combinedBinds = userBinds + themeBinds

    // Use the combined binds with the layout
    DynamicLayout(
        component = createLayoutComponent(profileLayoutJson),
        bindValue = combinedBinds
    )
}
```

### Using CompositionLocal for Hierarchical Binding

The library provides a `LocalBindsValue` CompositionLocal for accessing bind values throughout the
composition hierarchy:

```kotlin
@Composable
fun AppScreen() {
    // Create a top-level BindsValue
    val appBinds = remember { BindsValue() }
    appBinds.setValue("appName", "My App")
    appBinds.setValue("version", "1.2.3")

    // Provide it to the composition hierarchy
    CompositionLocalProvider(LocalBindsValue provides appBinds) {
        // Child composables can now access appBinds
        HomeScreen()
    }
}

@Composable
fun HomeScreen() {
    // Create a screen-level BindsValue
    val screenBinds = remember { BindsValue() }
    screenBinds.setValue("screenTitle", "Home")

    // Get the parent BindsValue from CompositionLocal
    val parentBinds = LocalBindsValue.current

    // Combine with parent binds
    val combinedBinds = screenBinds + parentBinds

    // Use the combined binds
    DynamicLayout(
        component = createLayoutComponent(homeLayoutJson),
        bindValue = combinedBinds
    )
}
```

## Real-World Examples

### User Profile Card

```kotlin
@Composable
fun UserProfileCard(user: User) {
    val bindsValue = remember { BindsValue() }

    LaunchedEffect(user) {
        bindsValue.setValue("name", user.displayName)
        bindsValue.setValue("email", user.email)
        bindsValue.setValue("initials", user.getInitials())
        bindsValue.setValue("memberType", user.membershipType)

        // Set color based on membership type
        val memberColor = when (user.membershipType) {
            "premium" -> "#FFD700"  // Gold
            "plus" -> "#C0C0C0"     // Silver
            else -> "#EEEEEE"       // Default
        }
        bindsValue.setValue("memberColor", memberColor)
    }

    val profileCardJson = """
    {
      "card": {
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
            "row": {
              "children": [
                {
                  "box": {
                    "modifier": {
                      "base": {
                        "size": 60,
                        "background": {
                          "color": "{memberColor}",
                          "shape": "circle"
                        }
                      },
                      "contentAlignment": "center"
                    },
                    "children": [
                      {
                        "text": {
                          "content": "{initials}",
                          "fontSize": 24,
                          "fontWeight": "bold"
                        }
                      }
                    ]
                  }
                },
                {
                  "spacer": {
                    "width": 16
                  }
                },
                {
                  "column": {
                    "children": [
                      {
                        "text": {
                          "content": "{name}",
                          "fontSize": 18,
                          "fontWeight": "bold"
                        }
                      },
                      {
                        "text": {
                          "content": "{email}",
                          "fontSize": 14
                        }
                      },
                      {
                        "text": {
                          "content": "{memberType} member",
                          "fontSize": 12,
                          "fontWeight": "medium",
                          "color": "{memberColor}"
                        }
                      }
                    ]
                  }
                }
              ]
            }
          }
        ]
      }
    }
    """

    DynamicLayout(
        component = createLayoutComponent(profileCardJson),
        bindValue = bindsValue
    )
}
```

### Real-Time Dashboard

```kotlin
@Composable
fun MetricsDashboard(viewModel: DashboardViewModel) {
    // Collect metrics state from ViewModel
    val metrics by viewModel.metricsFlow.collectAsState()

    val bindsValue = remember { BindsValue() }

    // Update binds when metrics change
    LaunchedEffect(metrics) {
        // Basic metrics
        bindsValue.setValue("activeUsers", metrics.activeUsers.toString())
        bindsValue.setValue("newSignups", metrics.newSignups.toString())
        bindsValue.setValue("totalRevenue", "$${metrics.revenue}")

        // Computed values
        val userChange = metrics.userChangePercentage
        bindsValue.setValue("userChangePercent", "${userChange.absoluteValue}%")
        bindsValue.setValue("userTrend", if (userChange >= 0) "▲" else "▼")
        bindsValue.setValue("userTrendColor", if (userChange >= 0) "#00AA00" else "#AA0000")

        // Format chart data
        bindsValue.setValue("chartData", metrics.weeklyData.joinToString(","))
    }

    DynamicLayout(
        component = createLayoutComponent(dashboardLayoutJson),
        bindValue = bindsValue
    )
}
```

## Best Practices

### 1. Use Descriptive Key Names

Choose clear, descriptive keys that indicate their purpose:

```kotlin
// Good
bindsValue.setValue("userFullName", user.fullName)
bindsValue.setValue("orderTotalPrice", "$${order.totalPrice}")

// Avoid
bindsValue.setValue("var1", user.fullName)
bindsValue.setValue("price", "$${order.totalPrice}")
```

### 2. Organize Related Values

Group related values using a consistent naming convention:

```kotlin
// User information
bindsValue.setValue("user_name", user.name)
bindsValue.setValue("user_email", user.email)
bindsValue.setValue("user_memberSince", user.joinDate.format("MMM yyyy"))

// Theme colors
bindsValue.setValue("color_primary", theme.primaryColor)
bindsValue.setValue("color_text", theme.textColor)
bindsValue.setValue("color_background", theme.backgroundColor)
```

### 3. Transform Data Before Binding

Process data into display-ready format before binding:

```kotlin
// Format values appropriately before binding
bindsValue.setValue("orderDate", order.date.format("MMM dd, yyyy"))
bindsValue.setValue("price", NumberFormat.getCurrencyInstance().format(product.price))
bindsValue.setValue("itemCount", "${cart.items.size} items")
```

### 4. Avoid Complex Logic in Templates

Keep the JSON templates focused on presentation, not business logic:

```kotlin
// Do this: Process in Kotlin, then bind the result
val status = when {
    order.isDelivered -> "Delivered"
    order.isShipped -> "Shipped on ${order.shipDate.format("MMM dd")}"
    order.isProcessing -> "Processing"
    else -> "Order received"
}
bindsValue.setValue("orderStatus", status)
bindsValue.setValue("statusColor", getStatusColor(order))

// Instead of trying to handle this in the template with multiple bindings
// This would make the JSON template more complex and brittle
```

### 5. Update Efficiently

Only update bind values when the source data actually changes:

```kotlin
// Good: Only update when user changes
LaunchedEffect(user.id) {
    bindsValue.setValue("username", user.displayName)
    // other user properties...
}

// Avoid: Updating unnecessarily on every recomposition
bindsValue.setValue("username", user.displayName)
```

### 6. Provide Fallbacks in JSON

When appropriate, include fallback values in your JSON:

```json
{
  "text": {
    "content": "{username|Guest}"
  }
}
```

*Note: This is a suggested feature not currently in the library. In the current implementation,
missing values stay as `{key}` in the output.*

## Troubleshooting

### Problem: Bind Value Not Updating

**Possible causes:**

- Value not set in the BindsValue instance
- Key mismatch between setValue and JSON
- BindsValue not passed to DynamicLayout

**Solutions:**

- Double-check key spelling and case
- Verify that setValue is actually called
- Make sure the same BindsValue instance is passed to DynamicLayout

### Problem: Changes Not Reflecting in UI

**Possible causes:**

- Missing LaunchedEffect dependency
- Using the wrong BindsValue instance
- State changes not triggering recomposition

**Solutions:**

- Add proper dependencies to LaunchedEffect
- Use remember to maintain BindsValue instance
- Make sure state changes trigger recomposition

## Next Steps

Now that you understand how to use bind values, learn about:

1. [Handling user actions](../06-bind-actions.md) with click events