Compose Remote Layout provides a powerful event handling system that allows you to create
interactive components controlled by your server-defined layouts. This guide covers how to implement
and manage click events in your dynamic UIs.

Click event handling in Compose Remote Layout enables you to create rich, interactive interfaces
that can be controlled and updated from your server. By combining click events with bind values and
custom components, you can build sophisticated UIs that adapt to user interactions and changing
requirements without requiring app updates.

## Basic Click Handling

### 1. Setting Up Click Handlers

The `DynamicLayout` composable accepts an `onClickHandler` parameter, which is a function that
receives a clickId string:

```kotlin
@Composable
fun ClickableLayout(layoutJson: String) {
    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            // Handle the click based on the ID
            when (clickId) {
                "login_button" -> performLogin()
                "signup_button" -> navigateToSignup()
                "settings_icon" -> openSettings()
                // Handle other click IDs
            }
        }
    )
}
```

### 2. Defining Clickable Components in JSON

You can make components clickable by adding a `clickId` to either:

1. The component itself (for buttons)
2. The component's base modifier (for other components)

#### Button Component Example

```json
{
  "button": {
    "content": "Sign In",
    "clickId": "login_button",
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    }
  }
}
```

#### Any Component with Clickable Modifier

```json
{
  "card": {
    "modifier": {
      "base": {
        "clickId": "open_details",
        "padding": {
          "all": 16
        }
      }
    },
    "children": [
      {
        "text": {
          "content": "Tap to see details"
        }
      }
    ]
  }
}
```

## Advanced Click Handling

### Creating Complex Interactive Layouts

You can combine click handlers with the bind value system to create sophisticated interactive UIs:

```kotlin
@Composable
fun InteractiveCounter() {
    val bindsValue = remember { BindsValue() }
    var counter by remember { mutableStateOf(0) }

    // Update binds when counter changes
    LaunchedEffect(counter) {
        bindsValue.setValue("counter", counter)
    }

    val layoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "padding": {
              "all": 16
            }
          },
          "verticalArrangement": "center",
          "horizontalAlignment": "center"
        },
        "children": [
          {
            "text": {
              "content": "Counter: {counter}",
              "fontSize": 24,
              "fontWeight": "bold"
            }
          },
          {
            "row": {
              "modifier": {
                "horizontalArrangement": "spaceEvenly"
              },
              "children": [
                {
                  "button": {
                    "content": "-",
                    "clickId": "decrement",
                    "fontSize": 20
                  }
                },
                {
                  "button": {
                    "content": "+",
                    "clickId": "increment",
                    "fontSize": 20
                  }
                }
              ]
            }
          },
          {
            "button": {
              "content": "Reset",
              "clickId": "reset",
              "modifier": {
                "base": {
                  "padding": {
                    "vertical": 8
                  }
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when (clickId) {
                "increment" -> counter++
                "decrement" -> if (counter > 0) counter--
                "reset" -> counter = 0
            }
        }
    )
}
```

### Implementing Navigation

Click handlers can be used to navigate between screens:

```kotlin
@Composable
fun NavigationMenu(
    navController: NavController,
    layoutJson: String
) {
    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            when (clickId) {
                "nav_home" -> navController.navigate("home")
                "nav_profile" -> navController.navigate("profile")
                "nav_settings" -> navController.navigate("settings")
                "nav_about" -> navController.navigate("about")
                "logout" -> {
                    // Perform logout operations
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
    )
}
```

### Handling Click Events with Parameters

While the basic click handler only provides the click ID, you can encode additional parameters in
the ID itself:

```json
{
  "text": {
    "content": "View Product",
    "modifier": {
      "base": {
        "clickId": "view_product:123"
      }
    }
  }
}
```

Then parse the parameters in your click handler:

```kotlin
DynamicLayout(
    component = component,
    onClickHandler = { clickId ->
        if (clickId.startsWith("view_product:")) {
            val productId = clickId.split(":")[1]
            navigateToProductDetail(productId)
        }
    }
)
```

## Best Practices

### 1. Use Consistent Click ID Patterns

Establish a naming convention for your click IDs to make your code more maintainable:

- Use prefixes to group related actions: `nav_`, `product_`, `user_`
- Use underscores to separate words: `add_to_cart`, `open_details`
- Use colons to separate parameters: `view_product:123`, `delete_item:456`

### 2. Delegate to View Model or Controller

For more complex applications, delegate click handling to a view model or controller:

```kotlin
@Composable
fun ProductScreen(viewModel: ProductViewModel) {
    DynamicLayout(
        component = createLayoutComponent(viewModel.layoutJson),
        bindValue = viewModel.bindsValue,
        onClickHandler = { clickId -> viewModel.handleClick(clickId) }
    )
}

class ProductViewModel : ViewModel() {
    val bindsValue = BindsValue()
    val layoutJson: String = // ... fetch from server or resource

    fun handleClick(clickId: String) {
        when {
            clickId.startsWith("view_product:") -> {
                val productId = clickId.split(":")[1]
                viewProductDetails(productId)
            }
            clickId == "filter_products" -> showFilterDialog()
            clickId == "sort_by_price" -> sortProductsByPrice()
            // Handle other clicks
        }
    }

    private fun viewProductDetails(productId: String) {
        // Implementation
    }

    private fun showFilterDialog() {
        // Implementation
    }

    private fun sortProductsByPrice() {
        // Implementation
    }
}
```

### 3. Combine with State Management

Integrate click handling with state management for more robust applications:

```kotlin
@Composable
fun ShoppingCartScreen(viewModel: CartViewModel) {
    val state by viewModel.state.collectAsState()
    val bindsValue = remember { BindsValue() }

    // Update binds when state changes
    LaunchedEffect(state) {
        bindsValue.setValue("itemCount", state.items.size.toString())
        bindsValue.setValue("totalPrice", state.formattedTotalPrice)
        bindsValue.setValue("isCheckoutEnabled", state.canCheckout.toString())
    }

    DynamicLayout(
        component = createLayoutComponent(state.cartLayoutJson),
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when {
                clickId == "checkout" -> viewModel.checkout()
                clickId == "clear_cart" -> viewModel.clearCart()
                clickId.startsWith("remove_item:") -> {
                    val itemId = clickId.split(":")[1]
                    viewModel.removeItem(itemId)
                }
                clickId.startsWith("quantity_plus:") -> {
                    val itemId = clickId.split(":")[1]
                    viewModel.incrementQuantity(itemId)
                }
                clickId.startsWith("quantity_minus:") -> {
                    val itemId = clickId.split(":")[1]
                    viewModel.decrementQuantity(itemId)
                }
            }
        }
    )
}
```

For more advanced usage and examples, check out
the [sample projects](https://github.com/utsmannn/compose-remote-layout/tree/master/samples) in the
GitHub repository.